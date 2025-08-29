package com.vitaltrip.vitaltrip.first_aid.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EmergencySymptomType {
    BLEEDING("출혈"),
    BURNS("화상"),
    FRACTURE("골절"),
    ALLERGIC_REACTION("알레르기 반응"),
    SEIZURE("발작"),
    HEATSTROKE("열사병"),
    HYPOTHERMIA("저체온증"),
    POISONING("중독"),
    BREATHING_DIFFICULTY("호흡곤란"),
    ANIMAL_BITE("동물 물림"),
    FALL_INJURY("낙상 부상");

    private final String description;
}
