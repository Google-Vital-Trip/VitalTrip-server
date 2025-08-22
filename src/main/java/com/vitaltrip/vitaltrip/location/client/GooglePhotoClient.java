package com.vitaltrip.vitaltrip.location.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class GooglePhotoClient {

    private final RestClient googlePhotoRestClient;
    private final ObjectMapper objectMapper;

    public String getPhotoUri(String photoName) {
        try {
            String photoJson = googlePhotoRestClient.get()
                    .uri("/" + photoName + "/media?maxWidthPx=400&maxHeightPx=400")
                    .retrieve()
                    .body(String.class);

            if (photoJson == null) {
                return null;
            }

            JsonNode jsonNode = objectMapper.readTree(photoJson);

            return jsonNode.get("photoUri").asText();

        } catch (Exception e) {
            log.warn("Failed to get photo URI for: {}", photoName, e);
            return null;
        }
    }
}
