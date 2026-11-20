package com.vitaltrip.vitaltrip.domain.firstaid.service;

import com.vitaltrip.vitaltrip.domain.firstaid.model.GuideId;
import java.util.Optional;

public interface SymptomRouter {

    Optional<GuideId> route(String symptomDetail);
}
