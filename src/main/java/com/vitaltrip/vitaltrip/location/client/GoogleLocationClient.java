package com.vitaltrip.vitaltrip.location.client;

import com.vitaltrip.vitaltrip.common.exception.CustomException;
import com.vitaltrip.vitaltrip.common.exception.ErrorType;
import com.vitaltrip.vitaltrip.location.dto.GoogleTextSearchResponse;
import com.vitaltrip.vitaltrip.location.dto.Location;
import com.vitaltrip.vitaltrip.location.dto.GoogleTextSearchRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleLocationClient {

    @Value("${google.api.location.default-field-mask}")
    private String defaultFieldMask;

    private final RestClient googleLocationRestClient;

    public GoogleTextSearchResponse textSearch(String keyword, Location location, Double radiusMeters, String language) {
        GoogleTextSearchRequest requestBody = createRequestBody(keyword, location, radiusMeters, language);

        try {
            return googleLocationRestClient.post()
                    .uri("/places:searchText")
                    .header("X-Goog-FieldMask", defaultFieldMask)
                    .body(requestBody)
                    .retrieve()
                    .body(GoogleTextSearchResponse.class);
        } catch (Exception e) {
            throw new CustomException(ErrorType.INTERNAL_SERVER_ERROR,
                    "Google Places API 호출 실패: " + e.getMessage());
        }
    }

    GoogleTextSearchRequest createRequestBody(String keyword, Location location, Double radiusMeters, String language) {
        GoogleTextSearchRequest.LocationBias locationBias = new GoogleTextSearchRequest.LocationBias(
                new GoogleTextSearchRequest.Circle(location, radiusMeters)
        );

        return new GoogleTextSearchRequest(
                keyword,
                locationBias,
                15,
                language,
                true
        );
    }

}
