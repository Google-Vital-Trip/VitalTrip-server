package com.vitaltrip.vitaltrip.domain.first_aid.guide;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public record LocalizedText(Map<String, String> values) {

    public LocalizedText {
        values = values == null
                ? Map.of()
                : Collections.unmodifiableMap(new LinkedHashMap<>(values));
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static LocalizedText of(Map<String, String> values) {
        return new LocalizedText(values);
    }

    public Optional<String> get(String lang) {
        String text = values.get(lang);
        return text == null || text.isBlank() ? Optional.empty() : Optional.of(text);
    }

    public boolean has(String lang) {
        return get(lang).isPresent();
    }

    @JsonValue
    public Map<String, String> values() {
        return values;
    }
}
