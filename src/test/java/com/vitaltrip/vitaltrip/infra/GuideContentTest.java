package com.vitaltrip.vitaltrip.infra;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.vitaltrip.vitaltrip.domain.first_aid.guide.Guide;
import com.vitaltrip.vitaltrip.domain.first_aid.guide.GuideItem;
import com.vitaltrip.vitaltrip.domain.first_aid.guide.GuideRegistry;
import com.vitaltrip.vitaltrip.domain.first_aid.guide.LocalizedText;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.vitaltrip.vitaltrip.infra.guide.GuideYamlLoader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

class GuideContentTest {

    private static final Set<String> ALLOWED_PLACEHOLDERS = Set.of("emergencyNumber");
    private static final Pattern PLACEHOLDER = Pattern.compile("\\{([^{}]*)}");

    private static List<Guide> guides;
    private static List<String> languages;

    @BeforeAll
    static void load() throws IOException {
        var resources = new PathMatchingResourcePatternResolver().getResources("classpath:first_aid_guide/guides/*.yml");
        guides = new GuideRegistry(GuideYamlLoader.create().load(resources)).all();

        try (InputStream in = new ClassPathResource("config/guide.yml").getInputStream()) {
            JsonNode config = new YAMLMapper().readTree(in);
            languages = new ArrayList<>();
            config.at("/guide/languages").forEach(lang -> languages.add(lang.asText()));
        }
    }

    @Test
    @DisplayName("지원 언어 텍스트가 모두 채워져 있다")
    void supportedLanguagesFilled() {
        List<String> blank = guides.stream()
                .flatMap(GuideContentTest::texts)
                .flatMap(t -> languages.stream()
                        .filter(lang -> !t.text().has(lang))
                        .map(lang -> t.path() + " [" + lang + "]"))
                .toList();

        assertThat(blank).as("비어 있는 텍스트").isEmpty();
    }

    @Test
    @DisplayName("모든 항목의 출처 키가 sources에 있다")
    void sourceKeysExist() {
        List<String> unknown = guides.stream()
                .flatMap(g -> items(g)
                        .filter(item -> !g.sources().containsKey(item.source()))
                        .map(item -> g.id() + "." + item.id() + " -> " + item.source()))
                .toList();

        assertThat(unknown).as("sources에 없는 출처 키").isEmpty();
    }

    @Test
    @DisplayName("허용된 치환자만 쓴다")
    void onlyAllowedPlaceholders() {
        List<String> invalid = guides.stream()
                .flatMap(GuideContentTest::texts)
                .flatMap(t -> t.text().values().entrySet().stream()
                        .filter(e -> e.getValue() != null)
                        .flatMap(e -> PLACEHOLDER.matcher(e.getValue()).results()
                                .map(m -> m.group(1))
                                .filter(name -> !ALLOWED_PLACEHOLDERS.contains(name))
                                .map(name -> t.path() + " [" + e.getKey() + "] {" + name + "}")))
                .toList();

        assertThat(invalid).as("허용되지 않은 치환자").isEmpty();
    }

    private record Text(String path, LocalizedText text) {
    }

    private static Stream<GuideItem> items(Guide g) {
        return Stream.of(
                g.steps().stream(),
                g.branches().stream().flatMap(b -> b.steps().stream()),
                g.donts().stream(),
                g.accessHelp().stream()
        ).flatMap(s -> s);
    }

    private static Stream<Text> texts(Guide g) {
        String id = g.id().name();
        return Stream.of(
                Stream.of(new Text(id + ".title", g.title()), new Text(id + ".keyAction", g.keyAction())),
                g.branches().stream().map(b -> new Text(id + "." + b.id() + ".condition", b.condition())),
                items(g).map(item -> new Text(id + "." + item.id(), item.text()))
        ).flatMap(s -> s);
    }
}
