package com.vitaltrip.vitaltrip.domain.first_aid.guide;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record Guide(
        GuideId id,
        int version,
        LocalizedText title,
        LocalizedText keyAction,
        List<GuideItem> steps,
        List<Branch> branches,
        List<GuideItem> donts,
        List<GuideItem> accessHelp,
        Map<String, Source> sources
) {

    public Guide {
        steps = immutable(steps);
        branches = immutable(branches);
        donts = immutable(donts);
        accessHelp = immutable(accessHelp);
        sources = sources == null
                ? Map.of()
                : Collections.unmodifiableMap(new LinkedHashMap<>(sources));
    }

    private static <T> List<T> immutable(List<T> list) {
        return list == null ? List.of() : List.copyOf(list);
    }
}
