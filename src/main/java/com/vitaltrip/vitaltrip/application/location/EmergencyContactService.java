package com.vitaltrip.vitaltrip.application.location;

import com.vitaltrip.vitaltrip.presentation.location.dto.EmergencyContact;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmergencyContactService {

    private static final Map<String, EmergencyContact> EMERGENCY_CONTACTS = new HashMap<>();

    static {
        // 아시아-태평양 지역
        EMERGENCY_CONTACTS.put("KR", EmergencyContact.of("119", "112", "119")); // 한국
        EMERGENCY_CONTACTS.put("JP", EmergencyContact.of("119", "110", "119")); // 일본
        EMERGENCY_CONTACTS.put("CN", EmergencyContact.of("119", "110", "120")); // 중국
        EMERGENCY_CONTACTS.put("TW", EmergencyContact.of("119", "110", "119")); // 대만
        EMERGENCY_CONTACTS.put("HK", EmergencyContact.of("999", "999", "999")); // 홍콩
        EMERGENCY_CONTACTS.put("SG", EmergencyContact.of("995", "999", "995")); // 싱가포르
        EMERGENCY_CONTACTS.put("MY", EmergencyContact.of("994", "999", "999")); // 말레이시아
        EMERGENCY_CONTACTS.put("TH", EmergencyContact.of("199", "191", "1669")); // 태국
        EMERGENCY_CONTACTS.put("VN", EmergencyContact.of("114", "113", "115")); // 베트남
        EMERGENCY_CONTACTS.put("PH", EmergencyContact.of("116", "117", "911")); // 필리핀
        EMERGENCY_CONTACTS.put("ID", EmergencyContact.of("113", "110", "118")); // 인도네시아
        EMERGENCY_CONTACTS.put("IN", EmergencyContact.of("101", "100", "108", "112")); // 인도
        EMERGENCY_CONTACTS.put("AU", EmergencyContact.of("000", "000", "000", "000")); // 호주
        EMERGENCY_CONTACTS.put("NZ", EmergencyContact.of("111", "111", "111", "111")); // 뉴질랜드

        // 북미
        EMERGENCY_CONTACTS.put("US", EmergencyContact.of("911", "911", "911", "911")); // 미국
        EMERGENCY_CONTACTS.put("CA", EmergencyContact.of("911", "911", "911", "911")); // 캐나다
        EMERGENCY_CONTACTS.put("MX", EmergencyContact.of("911", "911", "911", "911")); // 멕시코

        // 유럽 - EU 표준
        EMERGENCY_CONTACTS.put("GB", EmergencyContact.of("999", "999", "999", "112")); // 영국
        EMERGENCY_CONTACTS.put("IE", EmergencyContact.of("999", "999", "999", "112")); // 아일랜드
        EMERGENCY_CONTACTS.put("FR", EmergencyContact.of("18", "17", "15", "112")); // 프랑스
        EMERGENCY_CONTACTS.put("DE", EmergencyContact.of("112", "110", "112", "112")); // 독일
        EMERGENCY_CONTACTS.put("IT", EmergencyContact.of("115", "113", "118", "112")); // 이탈리아
        EMERGENCY_CONTACTS.put("ES", EmergencyContact.of("080", "091", "061", "112")); // 스페인
        EMERGENCY_CONTACTS.put("PT", EmergencyContact.of("117", "117", "112", "112")); // 포르투갈
        EMERGENCY_CONTACTS.put("NL", EmergencyContact.of("112", "112", "112", "112")); // 네덜란드
        EMERGENCY_CONTACTS.put("BE", EmergencyContact.of("112", "112", "112", "112")); // 벨기에
        EMERGENCY_CONTACTS.put("CH", EmergencyContact.of("118", "117", "144", "112")); // 스위스
        EMERGENCY_CONTACTS.put("AT", EmergencyContact.of("122", "133", "144", "112")); // 오스트리아
        EMERGENCY_CONTACTS.put("SE", EmergencyContact.of("112", "112", "112", "112")); // 스웨덴
        EMERGENCY_CONTACTS.put("NO", EmergencyContact.of("110", "112", "113", "112")); // 노르웨이
        EMERGENCY_CONTACTS.put("DK", EmergencyContact.of("112", "112", "112", "112")); // 덴마크
        EMERGENCY_CONTACTS.put("FI", EmergencyContact.of("112", "112", "112", "112")); // 핀란드
        EMERGENCY_CONTACTS.put("PL", EmergencyContact.of("998", "997", "999", "112")); // 폴란드
        EMERGENCY_CONTACTS.put("CZ", EmergencyContact.of("150", "158", "155", "112")); // 체코
        EMERGENCY_CONTACTS.put("SK", EmergencyContact.of("150", "158", "155", "112")); // 슬로바키아
        EMERGENCY_CONTACTS.put("HU", EmergencyContact.of("105", "107", "104", "112")); // 헝가리
        EMERGENCY_CONTACTS.put("RO", EmergencyContact.of("981", "955", "961", "112")); // 루마니아
        EMERGENCY_CONTACTS.put("BG", EmergencyContact.of("160", "166", "150", "112")); // 불가리아
        EMERGENCY_CONTACTS.put("HR", EmergencyContact.of("193", "192", "194", "112")); // 크로아티아
        EMERGENCY_CONTACTS.put("SI", EmergencyContact.of("112", "113", "112", "112")); // 슬로베니아
        EMERGENCY_CONTACTS.put("GR", EmergencyContact.of("199", "100", "166", "112")); // 그리스

        // 동유럽 및 러시아권
        EMERGENCY_CONTACTS.put("RU", EmergencyContact.of("101", "102", "103", "112")); // 러시아
        EMERGENCY_CONTACTS.put("UA", EmergencyContact.of("101", "102", "103")); // 우크라이나
        EMERGENCY_CONTACTS.put("BY", EmergencyContact.of("101", "102", "103")); // 벨라루스

        // 중동
        EMERGENCY_CONTACTS.put("TR", EmergencyContact.of("110", "155", "112", "112")); // 터키
        EMERGENCY_CONTACTS.put("IL", EmergencyContact.of("102", "100", "101")); // 이스라엘
        EMERGENCY_CONTACTS.put("AE", EmergencyContact.of("997", "999", "998")); // UAE
        EMERGENCY_CONTACTS.put("SA", EmergencyContact.of("998", "999", "997")); // 사우디아라비아
        EMERGENCY_CONTACTS.put("QA", EmergencyContact.of("999", "999", "999")); // 카타르
        EMERGENCY_CONTACTS.put("KW", EmergencyContact.of("777", "777", "777")); // 쿠웨이트
        EMERGENCY_CONTACTS.put("BH", EmergencyContact.of("999", "999", "999")); // 바레인
        EMERGENCY_CONTACTS.put("OM", EmergencyContact.of("999", "999", "999")); // 오만
        EMERGENCY_CONTACTS.put("JO", EmergencyContact.of("199", "191", "193")); // 요르단
        EMERGENCY_CONTACTS.put("LB", EmergencyContact.of("175", "112", "140")); // 레바논

        // 아프리카
        EMERGENCY_CONTACTS.put("ZA", EmergencyContact.of("10177", "10111", "10177")); // 남아프리카공화국
        EMERGENCY_CONTACTS.put("EG", EmergencyContact.of("180", "122", "123")); // 이집트
        EMERGENCY_CONTACTS.put("MA", EmergencyContact.of("15", "19", "15")); // 모로코
        EMERGENCY_CONTACTS.put("TN", EmergencyContact.of("198", "197", "190")); // 튀니지
        EMERGENCY_CONTACTS.put("KE", EmergencyContact.of("999", "999", "999")); // 케냐
        EMERGENCY_CONTACTS.put("NG", EmergencyContact.of("199", "199", "199")); // 나이지리아
        EMERGENCY_CONTACTS.put("GH", EmergencyContact.of("192", "191", "193")); // 가나

        // 남미
        EMERGENCY_CONTACTS.put("BR", EmergencyContact.of("193", "190", "192")); // 브라질
        EMERGENCY_CONTACTS.put("AR", EmergencyContact.of("100", "101", "107")); // 아르헨티나
        EMERGENCY_CONTACTS.put("CL", EmergencyContact.of("132", "133", "131")); // 칠레
        EMERGENCY_CONTACTS.put("CO", EmergencyContact.of("119", "112", "125")); // 콜롬비아
        EMERGENCY_CONTACTS.put("PE", EmergencyContact.of("116", "105", "117")); // 페루
        EMERGENCY_CONTACTS.put("VE", EmergencyContact.of("171", "171", "171")); // 베네수엘라
        EMERGENCY_CONTACTS.put("EC", EmergencyContact.of("102", "101", "131")); // 에콰도르
        EMERGENCY_CONTACTS.put("UY", EmergencyContact.of("104", "109", "105")); // 우루과이
        EMERGENCY_CONTACTS.put("PY", EmergencyContact.of("132", "911", "141")); // 파라과이
        EMERGENCY_CONTACTS.put("BO", EmergencyContact.of("119", "110", "118")); // 볼리비아

        // 기타 추가 국가들
        EMERGENCY_CONTACTS.put("IS", EmergencyContact.of("112", "112", "112", "112")); // 아이슬란드
        EMERGENCY_CONTACTS.put("LU", EmergencyContact.of("112", "112", "112", "112")); // 룩셈부르크
        EMERGENCY_CONTACTS.put("MT", EmergencyContact.of("112", "112", "112", "112")); // 몰타
        EMERGENCY_CONTACTS.put("CY", EmergencyContact.of("112", "112", "112", "112")); // 키프로스
    }

    public EmergencyContact getEmergencyContact(String countryCode) {
        if (countryCode == null || countryCode.trim().isEmpty()) {
            log.warn("국가 코드가 비어있습니다.");
            return null;
        }

        String upperCaseCountryCode = countryCode.trim().toUpperCase();

        return EMERGENCY_CONTACTS.get(upperCaseCountryCode);
    }
}
