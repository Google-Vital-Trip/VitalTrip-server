package com.vitaltrip.vitaltrip.application.first_aid;

import com.vitaltrip.vitaltrip.domain.first_aid.guide.GuideId;
import java.util.Optional;

public interface SymptomRouter {

    Optional<GuideId> route(String symptomDetail);
}
