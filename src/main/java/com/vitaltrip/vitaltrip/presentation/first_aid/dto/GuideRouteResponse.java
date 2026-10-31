package com.vitaltrip.vitaltrip.presentation.first_aid.dto;

import java.util.List;

public record GuideRouteResponse(
        Status status,
        String emergencyNumber,
        GuideResponse guide,
        List<GuideSummaryResponse> guides
) {

    public enum Status {
        MATCHED,
        UNKNOWN,
        UNAVAILABLE
    }

    public static GuideRouteResponse matched(GuideResponse guide) {
        return new GuideRouteResponse(Status.MATCHED, guide.emergencyNumber(), guide, null);
    }

    public static GuideRouteResponse unknown(String emergencyNumber, List<GuideSummaryResponse> guides) {
        return new GuideRouteResponse(Status.UNKNOWN, emergencyNumber, null, guides);
    }

    public static GuideRouteResponse unavailable(String emergencyNumber, List<GuideSummaryResponse> guides) {
        return new GuideRouteResponse(Status.UNAVAILABLE, emergencyNumber, null, guides);
    }
}
