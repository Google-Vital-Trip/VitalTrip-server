package com.vitaltrip.vitaltrip.application.first_aid;

import com.vitaltrip.vitaltrip.application.location.LocationService;
import com.vitaltrip.vitaltrip.common.exception.CustomException;
import com.vitaltrip.vitaltrip.common.exception.ErrorType;
import com.vitaltrip.vitaltrip.domain.first_aid.guide.GuideId;
import com.vitaltrip.vitaltrip.domain.first_aid.guide.GuideRegistry;
import com.vitaltrip.vitaltrip.infra.guide.config.GuideProperties;
import com.vitaltrip.vitaltrip.presentation.first_aid.dto.GuideResponse;
import com.vitaltrip.vitaltrip.presentation.first_aid.dto.GuideRouteResponse;
import com.vitaltrip.vitaltrip.presentation.first_aid.dto.GuideSummaryResponse;
import com.vitaltrip.vitaltrip.presentation.location.dto.CountryIdentificationRequest;
import com.vitaltrip.vitaltrip.presentation.location.dto.EmergencyContact;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FirstAidGuideService {

    private static final String DEFAULT_EMERGENCY_NUMBER = "112";

    private final GuideRegistry guideRegistry;
    private final SymptomRouter symptomRouter;
    private final LocationService locationService;
    private final GuideProperties guideProperties;

    public GuideRouteResponse route(String symptomDetail, Double latitude, Double longitude, String lang) {
        String language = resolveLanguage(lang);
        String emergencyNumber = findEmergencyNumber(latitude, longitude);

        Optional<GuideId> routed;
        try {
            routed = symptomRouter.route(symptomDetail);
        } catch (RuntimeException e) {
            log.warn("증상 라우팅 실패 - 가이드 목록으로 대체: {}", e.getClass().getSimpleName());
            return GuideRouteResponse.unavailable(emergencyNumber, summaries(language));
        }

        return routed
                .map(id -> GuideRouteResponse.matched(
                        GuideResponse.of(guideRegistry.get(id), language, emergencyNumber)))
                .orElseGet(() -> GuideRouteResponse.unknown(emergencyNumber, summaries(language)));
    }

    public List<GuideSummaryResponse> getGuides(String lang) {
        return summaries(resolveLanguage(lang));
    }

    public GuideResponse getGuide(String guideId, Double latitude, Double longitude, String lang) {
        return GuideResponse.of(
                guideRegistry.get(parseId(guideId)),
                resolveLanguage(lang),
                findEmergencyNumber(latitude, longitude));
    }

    private List<GuideSummaryResponse> summaries(String language) {
        return guideRegistry.all().stream()
                .map(guide -> GuideSummaryResponse.of(guide, language))
                .toList();
    }

    private String resolveLanguage(String requested) {
        List<String> supported = guideProperties.languages();
        if (requested != null) {
            String language = requested.split("[-_]")[0].toLowerCase(Locale.ROOT);
            if (supported.contains(language)) {
                return language;
            }
        }
        return supported.getFirst();
    }

    private String findEmergencyNumber(Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            return DEFAULT_EMERGENCY_NUMBER;
        }
        try {
            EmergencyContact contact = locationService
                    .identifyCountry(new CountryIdentificationRequest(latitude, longitude))
                    .emergencyContact();
            if (contact != null && contact.medical() != null) {
                return contact.medical();
            }
        } catch (RuntimeException e) {
            log.warn("국가 판별 실패 - 기본 응급번호 사용: {}", e.getClass().getSimpleName());
        }
        return DEFAULT_EMERGENCY_NUMBER;
    }

    private GuideId parseId(String guideId) {
        try {
            return GuideId.valueOf(guideId.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new CustomException(ErrorType.GUIDE_NOT_FOUND);
        }
    }
}
