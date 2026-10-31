package com.vitaltrip.vitaltrip.presentation.first_aid.dto;

import com.vitaltrip.vitaltrip.domain.first_aid.guide.Guide;
import com.vitaltrip.vitaltrip.domain.first_aid.guide.GuideId;

public record GuideSummaryResponse(GuideId id, String title) {

    public static GuideSummaryResponse of(Guide guide, String language) {
        return new GuideSummaryResponse(guide.id(), guide.title().get(language).orElse(guide.id().name()));
    }
}
