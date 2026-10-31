package com.vitaltrip.vitaltrip.presentation.first_aid;

import com.vitaltrip.vitaltrip.application.first_aid.FirstAidGuideService;
import com.vitaltrip.vitaltrip.presentation.first_aid.dto.GuideResponse;
import com.vitaltrip.vitaltrip.presentation.first_aid.dto.GuideRouteRequest;
import com.vitaltrip.vitaltrip.presentation.first_aid.dto.GuideRouteResponse;
import com.vitaltrip.vitaltrip.presentation.first_aid.dto.GuideSummaryResponse;
import com.vitaltrip.vitaltrip.presentation.health.dto.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/first-aid")
@RequiredArgsConstructor
public class FirstAidGuideController {

    private final FirstAidGuideService firstAidGuideService;

    @PostMapping("/route")
    public ApiResponse<GuideRouteResponse> route(@Valid @RequestBody GuideRouteRequest request, Locale locale) {
        return ApiResponse.success(firstAidGuideService.route(
                request.symptomDetail(), request.latitude(), request.longitude(), locale.getLanguage()));
    }

    @GetMapping("/guides")
    public ApiResponse<List<GuideSummaryResponse>> getGuides(Locale locale) {
        return ApiResponse.success(firstAidGuideService.getGuides(locale.getLanguage()));
    }

    @GetMapping("/guides/{guideId}")
    public ApiResponse<GuideResponse> getGuide(
            @PathVariable String guideId,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            Locale locale
    ) {
        return ApiResponse.success(firstAidGuideService.getGuide(
                guideId, latitude, longitude, locale.getLanguage()));
    }
}
