package com.vitaltrip.vitaltrip.domain.first_aid.guide;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class GuideRegistry {

    private final Map<GuideId, Guide> guides;

    public GuideRegistry(Map<GuideId, Guide> guides) {

        Set<GuideId> missing = EnumSet.allOf(GuideId.class);
        missing.removeAll(guides.keySet());
        if (!missing.isEmpty()) {
            throw new IllegalStateException("가이드가 없는 ID가 있습니다: " + missing);
        }
        guides.forEach((id, guide) -> {
            if (guide == null || guide.id() != id) {
                throw new IllegalStateException("키와 가이드 ID가 다릅니다: " + id);
            }
        });

        this.guides = Collections.unmodifiableMap(new EnumMap<>(guides));
    }

    public Guide get(GuideId id) {
        return guides.get(Objects.requireNonNull(id, "id"));
    }

    public List<Guide> all() {
        return List.copyOf(guides.values());
    }
}
