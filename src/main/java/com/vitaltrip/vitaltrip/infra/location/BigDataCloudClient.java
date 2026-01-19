package com.vitaltrip.vitaltrip.infra.location;

import com.vitaltrip.vitaltrip.common.exception.CustomException;
import com.vitaltrip.vitaltrip.common.exception.ErrorType;
import com.vitaltrip.vitaltrip.presentation.location.dto.BigDataCloudResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Component
@RequiredArgsConstructor
public class BigDataCloudClient {

    private final RestClient bigDataCloudRestClient;

    public BigDataCloudResponse reverseGeocode(Double latitude, Double longitude) {
        log.info("BigDataCloud reverse geocoding for coordinates: {}, {}", latitude, longitude);

        try {
            BigDataCloudResponse response = bigDataCloudRestClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path("/data/reverse-geocode-client")
                    .queryParam("latitude", latitude)
                    .queryParam("longitude", longitude)
                    .queryParam("localityLanguage", "en")
                    .build())
                .retrieve()
                .body(BigDataCloudResponse.class);

            if (response == null) {
                throw new CustomException(ErrorType.INTERNAL_SERVER_ERROR,
                    "BigDataCloud API 응답이 null입니다");
            }

            log.info("BigDataCloud reverse geocoding completed successfully for country: {}",
                response.countryName());

            return response;

        } catch (RestClientException e) {
            log.error("BigDataCloud API 호출 실패", e);
            throw new CustomException(ErrorType.INTERNAL_SERVER_ERROR,
                "BigDataCloud API 호출 실패: " + e.getMessage());
        } catch (Exception e) {
            log.error("국가 식별 중 예상치 못한 오류", e);
            throw new CustomException(ErrorType.INTERNAL_SERVER_ERROR,
                "국가 식별 실패: " + e.getMessage());
        }
    }
}
