package com.vitaltrip.vitaltrip.infra.gemini;

import com.vitaltrip.vitaltrip.common.exception.CustomException;
import com.vitaltrip.vitaltrip.common.exception.ErrorType;
import com.vitaltrip.vitaltrip.infra.gemini.config.GeminiClientProperties;
import com.vitaltrip.vitaltrip.infra.gemini.dto.GeminiResponse;

import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeminiClient {

    private final RestClient geminiRestClient;
    private final GeminiClientProperties properties;

    public String generateContent(String prompt) {
        log.info("Generating content with {} model, prompt length: {}", properties.model(), prompt.length());

        Map<String, Object> request = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)
                        ))
                )
        );

        try {
            GeminiResponse response = geminiRestClient.post()
                    .uri("/{model}:generateContent?key={apiKey}", properties.model(), properties.apiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(GeminiResponse.class);

            if (response == null) {
                throw new CustomException(ErrorType.INTERNAL_SERVER_ERROR,
                        "Gemini API 응답이 null입니다");
            }

            String generatedText = response.getGeneratedText();
            if (generatedText == null || generatedText.trim().isEmpty()) {
                throw new CustomException(ErrorType.INTERNAL_SERVER_ERROR,
                        "Gemini API 응답에서 텍스트를 추출할 수 없습니다");
            }

            log.info("Content generation completed successfully. Response length: {}",
                    generatedText.length());
            return generatedText;

        } catch (RestClientException e) {
            log.error("Gemini API 호출 실패", e);
            throw new CustomException(ErrorType.INTERNAL_SERVER_ERROR,
                    "Gemini API 호출 실패: " + e.getMessage());
        } catch (Exception e) {
            log.error("컨텐츠 생성 중 예상치 못한 오류", e);
            throw new CustomException(ErrorType.INTERNAL_SERVER_ERROR,
                    "컨텐츠 생성 실패: " + e.getMessage());
        }
    }
}
