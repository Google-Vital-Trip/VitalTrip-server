package com.vitaltrip.vitaltrip.presentation.location;

import com.vitaltrip.vitaltrip.presentation.health.dto.ApiResponse;
import com.vitaltrip.vitaltrip.presentation.location.docs.LocationControllerDocs;
import com.vitaltrip.vitaltrip.presentation.location.dto.CountryIdentificationRequest;
import com.vitaltrip.vitaltrip.presentation.location.dto.CountryIdentificationResponse;
import com.vitaltrip.vitaltrip.presentation.location.dto.Location;
import com.vitaltrip.vitaltrip.presentation.location.dto.NearbyPlaceRequest;
import com.vitaltrip.vitaltrip.presentation.location.dto.NearbyPlaceResponse;
import com.vitaltrip.vitaltrip.application.location.LocationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/location")
@RequiredArgsConstructor
public class LocationController implements LocationControllerDocs {

    private final LocationService locationService;

    @GetMapping("/nearby")
    @Override
    public ApiResponse<List<NearbyPlaceResponse>> searchNearbyPlaces(
        @RequestParam
        @DecimalMin(value = "-90.0", message = "위도는 -90에서 90 사이여야 합니다")
        @DecimalMax(value = "90.0", message = "위도는 -90에서 90 사이여야 합니다")
        Double latitude,

        @RequestParam
        @DecimalMin(value = "-180.0", message = "경도는 -180에서 180 사이여야 합니다")
        @DecimalMax(value = "180.0", message = "경도는 -180에서 180 사이여야 합니다")
        Double longitude,

        @RequestParam(defaultValue = "hospital")
        @Pattern(
            regexp = "^(hospital|pharmacy|emergency)$",
            message = "시설 타입은 hospital, pharmacy, emergency 중 하나여야 합니다"
        )
        String type,

        @RequestParam(defaultValue = "5000")
        @Min(value = 500, message = "검색 반경은 최소 500m 이상이어야 합니다")
        @Max(value = 10000, message = "검색 반경은 최대 10km 이하여야 합니다")
        Double radius,

        @RequestParam(defaultValue = "en")
        String language
    ) {
        NearbyPlaceRequest request = new NearbyPlaceRequest(
            new Location(latitude, longitude), type, radius, language
        );

        return ApiResponse.success(locationService.searchNearbyPlaces(request));
    }

    @PostMapping("/identify-country")
    @Override
    public ApiResponse<CountryIdentificationResponse> identifyCountry(
        @Valid @RequestBody CountryIdentificationRequest request
    ) {
        CountryIdentificationResponse response = locationService.identifyCountry(request);
        return ApiResponse.success(response);
    }
}

