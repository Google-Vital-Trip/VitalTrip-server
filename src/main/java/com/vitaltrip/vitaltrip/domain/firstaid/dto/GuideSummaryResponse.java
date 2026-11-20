package com.vitaltrip.vitaltrip.domain.firstaid.dto;

import com.vitaltrip.vitaltrip.domain.firstaid.model.Guide;
import com.vitaltrip.vitaltrip.domain.firstaid.model.GuideId;

public record GuideSummaryResponse(GuideId id, String title) {

    public static GuideSummaryResponse of(Guide guide, String language) {
        return new GuideSummaryResponse(guide.id(), guide.title().get(language).orElse(guide.id().name()));
    }
}
