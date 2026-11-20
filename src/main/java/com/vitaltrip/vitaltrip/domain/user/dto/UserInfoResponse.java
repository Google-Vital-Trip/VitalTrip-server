package com.vitaltrip.vitaltrip.domain.user.dto;

import java.time.LocalDate;

public record UserInfoResponse(
        Long id,
        String email,
        String name,
        LocalDate birthDate,
        String countryCode,
        String phoneNumber,
        String profileImageUrl
) {

}
