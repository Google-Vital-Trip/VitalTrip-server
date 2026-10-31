package com.vitaltrip.vitaltrip.infra.gemini;

import com.vitaltrip.vitaltrip.application.first_aid.SymptomRouter;
import com.vitaltrip.vitaltrip.domain.first_aid.guide.Branch;
import com.vitaltrip.vitaltrip.domain.first_aid.guide.GuideId;
import com.vitaltrip.vitaltrip.domain.first_aid.guide.GuideRegistry;
import com.vitaltrip.vitaltrip.infra.gemini.config.GeminiClientProperties;
import com.vitaltrip.vitaltrip.infra.gemini.config.GeminiRouterProperties;
import com.vitaltrip.vitaltrip.infra.gemini.dto.GeminiRequest;
import com.vitaltrip.vitaltrip.infra.gemini.dto.GeminiRequest.Content;
import com.vitaltrip.vitaltrip.infra.gemini.dto.GeminiRequest.GenerationConfig;
import com.vitaltrip.vitaltrip.infra.gemini.dto.GeminiRequest.Schema;
import com.vitaltrip.vitaltrip.infra.gemini.dto.GeminiRequest.ThinkingConfig;
import com.vitaltrip.vitaltrip.infra.gemini.dto.GeminiResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
public class GeminiSymptomRouter implements SymptomRouter {

    private static final String UNKNOWN = "UNKNOWN";
    private static final String GUIDES_PLACEHOLDER = "{guides}";

    private final RestClient geminiRestClient;
    private final GeminiClientProperties clientProperties;
    private final GeminiRouterProperties routerProperties;
    private final Content systemInstruction;
    private final GenerationConfig generationConfig;

    public GeminiSymptomRouter(
            RestClient geminiRestClient,
            GeminiClientProperties clientProperties,
            GeminiRouterProperties routerProperties,
            GuideRegistry guideRegistry,
            @Value("classpath:first_aid_guide/router-prompt.txt") Resource promptFile
    ) throws IOException {
        this.geminiRestClient = geminiRestClient;
        this.clientProperties = clientProperties;
        this.routerProperties = routerProperties;
        this.systemInstruction = Content.of(null, systemPrompt(promptFile, guideRegistry));
        this.generationConfig = new GenerationConfig(
                0.0,
                "text/x.enum",
                Schema.enumOf(allowedAnswers()),
                isBlank(routerProperties.thinkingLevel()) ? null : new ThinkingConfig(routerProperties.thinkingLevel()));
    }

    @Override
    @CircuitBreaker(name = "symptomRouter")
    public Optional<GuideId> route(String symptomDetail) {
        long start = System.nanoTime();
        GeminiResponse response = call(symptomDetail);
        Optional<GuideId> result = parse(response.getGeneratedText());

        var usage = response.getUsageMetadata();
        log.info("증상 라우팅 {} ({}ms, model={}, tokens in={} out={} thought={})",
                result.map(Enum::name).orElse(UNKNOWN),
                TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start),
                response.getModelVersion(),
                usage == null ? null : usage.getPromptTokenCount(),
                usage == null ? null : usage.getCandidatesTokenCount(),
                usage == null ? null : usage.getThoughtsTokenCount());
        return result;
    }

    private GeminiResponse call(String symptomDetail) {
        GeminiRequest request = new GeminiRequest(
                systemInstruction,
                List.of(Content.of("user", symptomDetail)),
                generationConfig);

        GeminiResponse response = geminiRestClient.post()
                .uri("/{model}:generateContent", routerProperties.model())
                .header("x-goog-api-key", clientProperties.apiKey())
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(GeminiResponse.class);

        if (response == null || response.getGeneratedText() == null) {
            throw new IllegalStateException("Gemini 응답에 결과가 없습니다");
        }
        return response;
    }

    static Optional<GuideId> parse(String answer) {
        String value = answer.strip().replace("\"", "");
        if (UNKNOWN.equals(value)) {
            return Optional.empty();
        }
        try {
            return Optional.of(GuideId.valueOf(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("허용되지 않은 라우팅 결과: " + value);
        }
    }

    static String systemPrompt(Resource promptFile, GuideRegistry guideRegistry) throws IOException {
        String template = promptFile.getContentAsString(StandardCharsets.UTF_8);
        if (!template.contains(GUIDES_PLACEHOLDER)) {
            throw new IllegalStateException("router-prompt.txt에 " + GUIDES_PLACEHOLDER + " 자리가 없습니다");
        }
        String guides = guideRegistry.all().stream()
                .map(guide -> {
                    String branches = guide.branches().stream()
                            .map(Branch::id)
                            .collect(Collectors.joining(", "));
                    return "- " + guide.id() + (branches.isEmpty() ? "" : " (" + branches + ")");
                })
                .collect(Collectors.joining("\n"));
        return template.replace(GUIDES_PLACEHOLDER, guides);
    }

    static List<String> allowedAnswers() {
        return Stream.concat(Arrays.stream(GuideId.values()).map(Enum::name), Stream.of(UNKNOWN)).toList();
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
