package com.vitaltrip.vitaltrip.presentation.first_aid.dto;

import com.vitaltrip.vitaltrip.domain.first_aid.guide.Guide;
import com.vitaltrip.vitaltrip.domain.first_aid.guide.GuideId;
import com.vitaltrip.vitaltrip.domain.first_aid.guide.GuideItem;
import com.vitaltrip.vitaltrip.domain.first_aid.guide.LocalizedText;
import com.vitaltrip.vitaltrip.domain.first_aid.guide.Source;
import java.util.List;
import java.util.Map;

public record GuideResponse(
        GuideId id,
        int version,
        String language,
        String emergencyNumber,
        String title,
        String keyAction,
        List<Item> steps,
        List<Branch> branches,
        List<Item> donts,
        List<Item> accessHelp,
        Map<String, Source> sources
) {

    private static final String PLACEHOLDER = "{emergencyNumber}";

    public record Item(String id, String text, String source) {
    }

    public record Branch(String id, String condition, List<Item> steps) {
    }

    public static GuideResponse of(Guide guide, String language, String emergencyNumber) {
        var renderer = new Renderer(language, emergencyNumber);
        return new GuideResponse(
                guide.id(),
                guide.version(),
                language,
                emergencyNumber,
                renderer.text(guide.title()),
                renderer.text(guide.keyAction()),
                renderer.items(guide.steps()),
                guide.branches().stream()
                        .map(b -> new Branch(b.id(), renderer.text(b.condition()), renderer.items(b.steps())))
                        .toList(),
                renderer.items(guide.donts()),
                renderer.items(guide.accessHelp()),
                guide.sources());
    }

    private record Renderer(String language, String emergencyNumber) {

        String text(LocalizedText text) {
            return text.get(language).orElse("").replace(PLACEHOLDER, emergencyNumber);
        }

        List<Item> items(List<GuideItem> items) {
            return items.stream()
                    .map(i -> new Item(i.id(), text(i.text()), i.source()))
                    .toList();
        }
    }
}
