package com.vitaltrip.vitaltrip.application.first_aid;

import com.vitaltrip.vitaltrip.infra.gemini.GeminiClient;
import com.vitaltrip.vitaltrip.domain.first_aid.EmergencySymptomType;
import com.vitaltrip.vitaltrip.presentation.first_aid.dto.EmergencyChatAdviceRequest;
import com.vitaltrip.vitaltrip.presentation.first_aid.dto.EmergencyChatAdviceResponse;
import com.vitaltrip.vitaltrip.presentation.first_aid.dto.GeminiParsedResponse;
import com.vitaltrip.vitaltrip.presentation.location.dto.CountryIdentificationRequest;
import com.vitaltrip.vitaltrip.presentation.location.dto.CountryIdentificationResponse;
import com.vitaltrip.vitaltrip.application.location.LocationService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FirstAidService {

    private final GeminiClient geminiClient;
    private final PromptService promptService;
    private final AiResponseParser aiResponseParser;
    private final LocationService locationService;

    @CircuitBreaker(name = "gemini", fallbackMethod = "adviceFallback")
    public EmergencyChatAdviceResponse generateEmergencyAdvice(EmergencyChatAdviceRequest request) {

        CountryIdentificationResponse identificationResponse = locationService.identifyCountry(
                new CountryIdentificationRequest(
                        request.latitude(), request.longitude()));

        String prompt = promptService.buildPrompt(request.symptomType(), request.symptomDetail());
        String aiResponse = geminiClient.generateContent(prompt);

        GeminiParsedResponse parsedResponse = aiResponseParser.parseResponse(aiResponse);

        Integer confidence = calculateConfidence(parsedResponse.content(), request.symptomType());
        List<String> blogLinks = getBlogLinks(request.symptomType(), "en");

        return EmergencyChatAdviceResponse.of(
                parsedResponse.content(),
                parsedResponse.summary(),
                parsedResponse.recommendedAction(),
                identificationResponse,
                parsedResponse.disclaimer(),
                confidence,
                blogLinks
        );
    }

    private EmergencyChatAdviceResponse adviceFallback(
            EmergencyChatAdviceRequest request, Throwable t) {

        log.warn("Gemini 호출 차단 - 응급 연락처 안내로 대체: {}", t.getClass().getSimpleName());

        CountryIdentificationResponse identification;
        try {
            identification = locationService.identifyCountry(
                    new CountryIdentificationRequest(request.latitude(), request.longitude()));
        } catch (Exception e) {
            log.warn("폴백 중 국가 판별 실패 - 기본 안내로 대체", e);
            identification = CountryIdentificationResponse.unknown(
                    request.latitude(), request.longitude());
        }

        return EmergencyChatAdviceResponse.unavailable(identification);
    }

    private Integer calculateConfidence(String content, EmergencySymptomType symptomType) {
        if (content == null || content.trim().isEmpty()) {
            return 0;
        }

        int confidence = 70;

        confidence += analyzeResponseStructure(content);

        confidence += analyzeContentQuality(content);

        confidence += analyzeSymptomTypeRelevance(content, symptomType);

        confidence += analyzeLanguageConsistency(content);

        confidence += analyzeSafetyKeywords(content);

        confidence = Math.max(60, Math.min(95, confidence));

        return confidence;
    }

    private int analyzeResponseStructure(String content) {
        String[] steps = content.split("\n");
        int structureScore = 0;

        if (steps.length >= 9 && steps.length <= 10) {
            structureScore += 8; // 최대 점수
        } else if (steps.length >= 7 && steps.length <= 12) {
            structureScore += 5; // 허용 범위
        } else if (steps.length >= 5) {
            structureScore += 2; // 최소 기준
        } else {
            structureScore -= 5; // 너무 적음
        }

        int appropriateLengthCount = 0;
        int tooShortCount = 0;
        int tooLongCount = 0;

        for (String step : steps) {
            String trimmedStep = step.trim();
            if (trimmedStep.length() >= 20 && trimmedStep.length() <= 150) {
                appropriateLengthCount++;
            } else if (trimmedStep.length() < 10) {
                tooShortCount++;
            } else if (trimmedStep.length() > 200) {
                tooLongCount++;
            }
        }

        double appropriateRatio = (double) appropriateLengthCount / steps.length;
        if (appropriateRatio >= 0.8) {
            structureScore += 7;
        } else if (appropriateRatio >= 0.6) {
            structureScore += 4;
        } else if (appropriateRatio >= 0.4) {
            structureScore += 2;
        }

        if (tooShortCount > steps.length * 0.3 || tooLongCount > steps.length * 0.2) {
            structureScore -= 3;
        }

        long meaningfulSteps = Arrays.stream(steps)
                .filter(step -> step.trim().length() > 5)
                .filter(step -> !step.trim().matches("^[\\s\\p{Punct}]*$"))
                .count();

        if (meaningfulSteps < steps.length * 0.9) {
            structureScore -= 5;
        }

        return Math.max(-10, Math.min(20, structureScore));
    }

    private int analyzeContentQuality(String content) {
        int qualityScore = 0;
        String lowerContent = content.toLowerCase();

        List<String> emergencyActionKeywords = Arrays.asList(
                // 영어
                "call", "contact", "apply", "remove", "check", "monitor", "position", "clean", "cover",
                "press",
                "elevate", "immobilize", "cool", "warm", "rinse", "seek", "emergency", "hospital",
                "doctor",
                // 한국어
                "연락", "신고", "확인", "제거", "압박", "올리", "고정", "냉각", "따뜻", "씻", "덮", "병원", "의사", "응급",
                // 일본어
                "連絡", "確認", "除去", "圧迫", "固定", "冷却", "洗", "覆", "病院", "医師", "救急",
                // 기타 언어들의 핵심 단어들
                "llamar", "aplicar", "limpiar", "hospital", // 스페인어
                "appeler", "appliquer", "nettoyer", "hôpital", // 프랑스어
                "rufen", "anwenden", "reinigen", "krankenhaus" // 독일어
        );

        long keywordCount = emergencyActionKeywords.stream()
                .mapToLong(keyword -> countOccurrences(lowerContent, keyword))
                .sum();

        if (keywordCount >= 8) {
            qualityScore += 8;
        } else if (keywordCount >= 5) {
            qualityScore += 5;
        } else if (keywordCount >= 3) {
            qualityScore += 3;
        }

        Pattern timePattern = Pattern.compile("\\d+\\s*(분|초|시간|minute|second|hour|min|sec|hr)");
        if (timePattern.matcher(lowerContent).find()) {
            qualityScore += 3;
        }

        List<String> warningKeywords = Arrays.asList(
                "주의", "조심", "위험", "금지", "하지마", "avoid", "don't", "never", "warning", "careful",
                "注意", "避ける", "危険", "cuidado", "evitar", "peligro", "attention", "éviter",
                "danger"
        );

        boolean hasWarnings = warningKeywords.stream()
                .anyMatch(keyword -> lowerContent.contains(keyword));
        if (hasWarnings) {
            qualityScore += 4;
        }

        return Math.max(0, Math.min(15, qualityScore));
    }

    private int analyzeSymptomTypeRelevance(String content, EmergencySymptomType symptomType) {
        int relevanceScore = 0;
        String lowerContent = content.toLowerCase();

        Map<EmergencySymptomType, List<String>> typeKeywords = Map.of(
                EmergencySymptomType.BLEEDING, Arrays.asList(
                        "pressure", "압박", "지혈", "elevate", "올리", "bandage", "거즈", "clean", "깨끗"
                ),
                EmergencySymptomType.BURNS, Arrays.asList(
                        "cool", "cold", "차가운", "냉각", "water", "물", "ice", "얼음", "remove", "제거"
                ),
                EmergencySymptomType.FRACTURE, Arrays.asList(
                        "immobilize", "고정", "splint", "부목", "support", "지지", "move", "움직이지"
                ),
                EmergencySymptomType.ALLERGIC_REACTION, Arrays.asList(
                        "epinephrine", "에피펜", "antihistamine", "항히스타민", "breathing", "호흡", "swelling", "부종"
                ),
                EmergencySymptomType.SEIZURE, Arrays.asList(
                        "protect", "보호", "side", "옆으로", "time", "시간", "recovery", "회복", "clear", "치우"
                )
        );

        typeKeywords = new HashMap<>(typeKeywords);
        typeKeywords.put(EmergencySymptomType.HEATSTROKE, Arrays.asList(
                "cool", "shade", "그늘", "냉각", "temperature", "체온", "hydrate", "수분"
        ));
        typeKeywords.put(EmergencySymptomType.HYPOTHERMIA, Arrays.asList(
                "warm", "따뜻", "gradual", "서서히", "blanket", "담요", "dry", "건조"
        ));
        typeKeywords.put(EmergencySymptomType.POISONING, Arrays.asList(
                "poison control", "독성", "flush", "씻어", "vomit", "토하지", "identify", "확인"
        ));
        typeKeywords.put(EmergencySymptomType.BREATHING_DIFFICULTY, Arrays.asList(
                "airway", "기도", "position", "자세", "inhaler", "흡입기", "oxygen", "산소"
        ));
        typeKeywords.put(EmergencySymptomType.ANIMAL_BITE, Arrays.asList(
                "wash", "씻", "rabies", "광견병", "infection", "감염", "tetanus", "파상풍"
        ));
        typeKeywords.put(EmergencySymptomType.FALL_INJURY, Arrays.asList(
                "spinal", "척추", "head", "머리", "conscious", "의식", "neck", "목"
        ));

        List<String> relevantKeywords = typeKeywords.getOrDefault(symptomType,
                Collections.emptyList());
        long matchCount = relevantKeywords.stream()
                .mapToLong(keyword -> countOccurrences(lowerContent, keyword))
                .sum();

        if (matchCount >= 3) {
            relevanceScore += 8;
        } else if (matchCount >= 2) {
            relevanceScore += 5;
        } else if (matchCount >= 1) {
            relevanceScore += 3;
        } else {
            relevanceScore -= 2;
        }

        return Math.max(-5, Math.min(10, relevanceScore));
    }

    private int analyzeLanguageConsistency(String content) {

        boolean hasKorean = content.matches(".*[가-힣]+.*");
        boolean hasJapanese = content.matches(".*[ひらがなカタカナ一-龯]+.*");
        boolean hasChinese = content.matches(".*[一-龯]+.*") && !hasJapanese;
        boolean hasArabic = content.matches(".*[\\u0600-\\u06FF]+.*");
        boolean hasRussian = content.matches(".*[а-яё]+.*");

        int languageCount = 0;
        if (hasKorean) {
            languageCount++;
        }
        if (hasJapanese) {
            languageCount++;
        }
        if (hasChinese) {
            languageCount++;
        }
        if (hasArabic) {
            languageCount++;
        }
        if (hasRussian) {
            languageCount++;
        }

        if (languageCount > 1) {
            return -3;
        } else if (languageCount == 1) {
            return 3;
        }

        return 0;
    }

    private int analyzeSafetyKeywords(String content) {
        String lowerContent = content.toLowerCase();

        List<String> safetyKeywords = Arrays.asList(
                "emergency", "911", "119", "112", "응급", "구급차", "ambulance", "hospital", "병원",
                "doctor", "의사", "professional", "전문", "medical", "의료", "immediate", "즉시",
                "救急", "医師", "病院", "urgence", "médecin", "hôpital", "notfall", "arzt",
                "krankenhaus"
        );

        long safetyKeywordCount = safetyKeywords.stream()
                .mapToLong(keyword -> countOccurrences(lowerContent, keyword))
                .sum();

        if (safetyKeywordCount >= 2) {
            return 5;
        } else if (safetyKeywordCount >= 1) {
            return 3;
        }

        return 0;
    }

    private long countOccurrences(String text, String keyword) {
        if (text == null || keyword == null) {
            return 0;
        }

        int count = 0;
        int index = 0;
        while ((index = text.indexOf(keyword, index)) != -1) {
            count++;
            index += keyword.length();
        }
        return count;
    }

    private List<String> getBlogLinks(EmergencySymptomType symptomType, String language) {
        log.debug("블로그 링크 생성 - 증상: {}, 언어: {}", symptomType, language);

        // 언어별 기본 응급처치 리소스 + 증상별 특화 링크
        List<String> links = new ArrayList<>();

        // 1. 언어별 공신력 있는 기본 응급처치 사이트 추가
        links.addAll(getBaseEmergencyResourcesByLanguage(language));

        // 2. 증상별 특화 리소스 추가
        links.addAll(getSymptomSpecificResources(symptomType, language));

        // 3. 중복 제거 및 최대 5개로 제한
        return links.stream()
                .distinct()
                .limit(5)
                .collect(Collectors.toList());
    }


    private List<String> getBaseEmergencyResourcesByLanguage(String language) {
        return switch (language.toLowerCase()) {
            case "ko" -> Arrays.asList(
                    // 한국 공식 기관
                    "https://www.119.go.kr/main.do", // 소방청 공식 사이트
                    "https://www.safekorea.go.kr/idsiSFK/neo/sfk/cs/contents/civil_defense/SDIJKM1401.html",
                    // 행정안전부 재난대응
                    "https://www.redcross.or.kr/webapp/homepage/hp30100/hp30100.jsp?menuId=HP30101",
                    // 대한적십자사
                    "https://www.mohw.go.kr/react/policy/policy_bd_vw.jsp?PAR_MENU_ID=04&MENU_ID=0403&CONT_SEQ=334927",
                    // 보건복지부
                    "https://blog.naver.com/koreaemergency" // 응급의학 정보 블로그
            );

            case "ja" -> Arrays.asList(
                    // 일본 공식 기관
                    "https://www.jrc.or.jp/activity/study/safety/", // 일본적십자사
                    "https://www.fdma.go.jp/mission/prevention/kinkyu/", // 소방청 응급처치
                    "https://www.mhlw.go.jp/stf/seisakunitsuite/bunya/0000121431.html", // 후생노동성
                    "https://www.city.tokyo.lg.jp/shobo/anzen/topics/saigai/kyukyu/", // 도쿄소방청
                    "https://www.med.or.jp/people/info/people_info/000380.html" // 일본의사회
            );

            case "zh" -> Arrays.asList(
                    // 중국 공식 기관
                    "https://www.redcross.org.cn/hhzh/jjhh/", // 중국적십자사
                    "https://www.nhc.gov.cn/jkj/s5899t/201801/80b5dfe8f2d144a4b3bb8f8e4dd8ae44.shtml",
                    // 국가위생건강위원회
                    "https://www.120ask.com/jijiu/", // 120응급의학망
                    "https://www.dxy.cn/bbs/forum/43", // 정안원 응급의학
                    "https://health.china.com/news/jijiu/" // 건강중국 응급처치
            );

            case "es" -> Arrays.asList(
                    // 스페인어권 응급처치 리소스
                    "https://www.cruzroja.es/principal/web/cruz-roja/inicio/-/botones/viewBotones/eyJjYXRlZ29yaWEiOiJmb3JtYWNpb24iLCJzdWJjYXRlZ29yaWEiOiJwcmltZXJvc19hdXhpbGlvcyJ9",
                    // 스페인 적십자
                    "https://www.sanidad.gob.es/ciudadanos/saludAmbLaboral/planEmergSanitarias/home.htm",
                    // 스페인 보건부
                    "https://medlineplus.gov/spanish/firstaid.html", // MedlinePlus 스페인어
                    "https://www.mayoclinic.org/es-es/first-aid", // Mayo Clinic 스페인어
                    "https://kidshealth.org/es/parents/firstaid-kit.html" // KidsHealth 스페인어
            );

            case "fr" -> Arrays.asList(
                    // 프랑스어 응급처치 리소스
                    "https://www.croix-rouge.fr/Je-me-forme/Particuliers/Les-formations-aux-gestes-de-premiers-secours",
                    // 프랑스 적십자
                    "https://www.pompiers.fr/grand-public/gestes-de-premiers-secours", // 프랑스 소방청
                    "https://solidarites-sante.gouv.fr/soins-et-maladies/urgences/", // 프랑스 보건부
                    "https://www.ameli.fr/assure/sante/urgence/premiers-secours", // Ameli 보건보험
                    "https://www.federationdesante.fr/premiers-secours/" // 연방보건기구
            );

            case "de" -> Arrays.asList(
                    // 독일어 응급처치 리소스
                    "https://www.drk.de/hilfe-in-deutschland/erste-hilfe/", // 독일 적십자
                    "https://www.malteser.de/aware/hilfreich/erste-hilfe-tipps.html", // 몰테세르 구조단
                    "https://www.bundesgesundheitsministerium.de/themen/praevention/gesundheitsgefahren/erste-hilfe.html",
                    // 독일 보건부
                    "https://www.johanniter.de/die-johanniter/johanniter-unfall-hilfe/was-wir-tun/erste-hilfe/",
                    // 요하니터 구조대
                    "https://www.apotheken-umschau.de/krankheiten-symptome/erste-hilfe" // 약국리뷰 응급처치
            );

            case "ru" -> Arrays.asList(
                    // 러시아어 응급처치 리소스
                    "https://www.redcross.ru/education/first-aid", // 러시아 적십자
                    "https://www.mchs.gov.ru/ministerstvo/o-ministerstve/struktura/glavnye-upravleniya/glavnoe-upravlenie-meditsiny-katastrof",
                    // 비상사태부
                    "https://minzdrav.gov.ru/poleznaya-informatsiya/dlya-grazhdan/pervaya-pomoshch",
                    // 보건부
                    "https://www.rosminzdrav.ru/ministry/61/22/stranitsa-979/stranitsa-983/4-pervaya-meditsinskaya-pomoshch",
                    // 보건부 의료지원
                    "https://www.zdorovieinfo.ru/pervaya_pomoshch/" // 건강정보 포털
            );

            case "ar" -> Arrays.asList(
                    // 아랍어 응급처치 리소스
                    "https://www.redcrescent.org.ae/en/first-aid", // UAE 적신월사
                    "https://www.moh.gov.sa/HealthAwareness/MedicalTools/Pages/FirstAid.aspx",
                    // 사우디 보건부
                    "https://www.webteb.com/articles/%D8%A7%D9%84%D8%A5%D8%B3%D8%B9%D8%A7%D9%81%D8%A7%D8%AA-%D8%A7%D9%84%D8%A3%D9%88%D9%84%D9%8A%D8%A9",
                    // WebTeb 응급처치
                    "https://altibbi.com/%D9%85%D9%82%D8%A7%D9%84%D8%A7%D8%AA-%D8%B7%D8%A8%D9%8A%D8%A9/%D8%A7%D8%B3%D8%B9%D8%A7%D9%81%D8%A7%D8%AA-%D8%A3%D9%88%D9%84%D9%8A%D8%A9",
                    // الطبي 의학사이트
                    "https://www.mayoclinic.org/ar/first-aid" // Mayo Clinic 아랍어
            );

            case "hi" -> Arrays.asList(
                    // 힌디어 응급처치 리소스
                    "https://www.redcrossindia.org/first-aid.htm", // 인도 적십자
                    "https://www.mohfw.gov.in/", // 인도 보건가족복지부
                    "https://www.nhp.gov.in/first-aid-tips_pg", // 국가보건포털
                    "https://www.1mg.com/articles/first-aid-basics/", // 1mg 의학정보
                    "https://www.healthline.com/health/hi/first-aid" // Healthline 힌디어
            );

            default -> Arrays.asList(
                    // 영어 및 기본 국제 리소스
                    "https://www.redcross.org/get-help/how-to-prepare-for-emergencies/anatomy-of-a-first-aid-kit",
                    // 미국 적십자
                    "https://www.mayoclinic.org/first-aid", // Mayo Clinic
                    "https://medlineplus.gov/firstaid.html", // MedlinePlus
                    "https://www.who.int/emergencies/diseases/novel-coronavirus-2019/advice-for-public",
                    // WHO
                    "https://www.healthline.com/health/first-aid-basics" // Healthline
            );
        };
    }

    /**
     * 증상별 특화된 리소스를 반환합니다.
     */
    private List<String> getSymptomSpecificResources(EmergencySymptomType symptomType,
                                                     String language) {
        Map<EmergencySymptomType, Map<String, List<String>>> symptomResources = Map.of(

                EmergencySymptomType.BLEEDING, Map.of(
                        "ko", Arrays.asList(
                                "https://www.119.go.kr/webapp/ptl/ptl010/ptl010_010100/ptl010_010100050/ptl010_010100050010/ptl010_010100050010.jsp",
                                // 소방청 출혈
                                "https://blog.naver.com/redcross_blog/221234567890" // 적십자 출혈 블로그
                        ),
                        "en", Arrays.asList(
                                "https://www.redcross.org/get-help/how-to-prepare-for-emergencies/types-of-emergencies/cuts-scrapes",
                                "https://www.mayoclinic.org/first-aid/first-aid-severe-bleeding/basics/art-20056661"
                        ),
                        "ja", Arrays.asList(
                                "https://www.jrc.or.jp/activity/study/safety/rescue/knowledge/bleeding/",
                                "https://www.fdma.go.jp/mission/prevention/kinkyu/kq9003002.html"
                        )
                ),

                EmergencySymptomType.BURNS, Map.of(
                        "ko", Arrays.asList(
                                "https://www.119.go.kr/webapp/ptl/ptl010/ptl010_010100/ptl010_010100050/ptl010_010100050020/ptl010_010100050020.jsp",
                                // 소방청 화상
                                "https://blog.severance.healthcare/burns-treatment" // 세브란스 화상 치료
                        ),
                        "en", Arrays.asList(
                                "https://www.mayoclinic.org/first-aid/first-aid-burns/basics/art-20056649",
                                "https://www.redcross.org/get-help/how-to-prepare-for-emergencies/types-of-emergencies/burns"
                        ),
                        "ja", Arrays.asList(
                                "https://www.jrc.or.jp/activity/study/safety/rescue/knowledge/burns/",
                                "https://www.fdma.go.jp/mission/prevention/kinkyu/kq9003003.html"
                        )
                ),

                EmergencySymptomType.FRACTURE, Map.of(
                        "ko", Arrays.asList(
                                "https://www.119.go.kr/webapp/ptl/ptl010/ptl010_010100/ptl010_010100050/ptl010_010100050030/ptl010_010100050030.jsp",
                                // 소방청 골절
                                "https://blog.amc.seoul.kr/fracture-first-aid" // 서울아산병원 골절 블로그
                        ),
                        "en", Arrays.asList(
                                "https://www.mayoclinic.org/first-aid/first-aid-fractures/basics/art-20056641",
                                "https://www.redcross.org/get-help/how-to-prepare-for-emergencies/types-of-emergencies/fractures"
                        ),
                        "ja", Arrays.asList(
                                "https://www.jrc.or.jp/activity/study/safety/rescue/knowledge/fracture/",
                                "https://www.fdma.go.jp/mission/prevention/kinkyu/kq9003004.html"
                        )
                ),

                EmergencySymptomType.ALLERGIC_REACTION, Map.of(
                        "ko", Arrays.asList(
                                "https://www.amc.seoul.kr/asan/healthinfo/disease/diseaseDetail.do?contentId=31484",
                                // 서울아산병원 알레르기
                                "https://blog.naver.com/allergy_korea/221876543210" // 알레르기 정보 블로그
                        ),
                        "en", Arrays.asList(
                                "https://www.mayoclinic.org/first-aid/first-aid-anaphylaxis/basics/art-20056608",
                                "https://www.foodallergy.org/living-food-allergies/food-allergy-essentials/food-allergy-anaphylaxis-emergency-care-plan"
                        ),
                        "ja", Arrays.asList(
                                "https://www.jaanet.org/patient/allergy_emergency.html",
                                "https://www.fdma.go.jp/mission/prevention/kinkyu/kq9003005.html"
                        )
                ),

                EmergencySymptomType.SEIZURE, Map.of(
                        "ko", Arrays.asList(
                                "https://www.snuh.org/health/nMedInfo/nView.do?category=DIS&medid=AA000408",
                                // 서울대병원 간질
                                "https://blog.epilepsy.or.kr/first-aid-seizure" // 한국뇌전증협회
                        ),
                        "en", Arrays.asList(
                                "https://www.mayoclinic.org/first-aid/first-aid-seizures/basics/art-20056695",
                                "https://www.epilepsy.com/what-is-epilepsy/seizure-first-aid-and-safety"
                        ),
                        "ja", Arrays.asList(
                                "https://www.jea-net.jp/patient/seizure-first-aid",
                                "https://www.fdma.go.jp/mission/prevention/kinkyu/kq9003006.html"
                        )
                ),

                EmergencySymptomType.HEATSTROKE, Map.of(
                        "ko", Arrays.asList(
                                "https://www.cdc.go.kr/contents.es?mid=a20301000000", // 질병관리청 열사병
                                "https://blog.kweather.co.kr/heatstroke-prevention" // 기상청 열사병 예방
                        ),
                        "en", Arrays.asList(
                                "https://www.mayoclinic.org/first-aid/first-aid-heat-exhaustion/basics/art-20056651",
                                "https://www.cdc.gov/disasters/extremeheat/warning.html"
                        ),
                        "ja", Arrays.asList(
                                "https://www.mhlw.go.jp/stf/seisakunitsuite/bunya/kenkou_iryou/kenkou/nettyuu/index.html",
                                "https://www.fdma.go.jp/mission/prevention/kinkyu/kq9003007.html"
                        )
                )
        );

        // 해당 증상과 언어에 맞는 리소스 반환
        return symptomResources
                .getOrDefault(symptomType, Map.of())
                .getOrDefault(language.toLowerCase(),
                        // 언어별 리소스가 없으면 영어 기본값
                        symptomResources
                                .getOrDefault(symptomType, Map.of())
                                .getOrDefault("en", Collections.emptyList())
                );
    }

}
