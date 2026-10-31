package com.vitaltrip.vitaltrip.domain.first_aid.guide;

import java.util.List;

public record Branch(String id, LocalizedText condition, List<GuideItem> steps) {

    public Branch {
        steps = steps == null ? List.of() : List.copyOf(steps);
    }
}
