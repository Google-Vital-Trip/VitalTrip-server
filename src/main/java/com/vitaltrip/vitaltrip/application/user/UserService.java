package com.vitaltrip.vitaltrip.application.user;

import com.vitaltrip.vitaltrip.domain.user.User;
import com.vitaltrip.vitaltrip.presentation.user.dto.ProfileUpdateRequest;
import com.vitaltrip.vitaltrip.presentation.user.dto.UserInfoResponse;
import com.vitaltrip.vitaltrip.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public UserInfoResponse getUserInfo(User user) {
        return new UserInfoResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getBirthDate(),
                user.getCountryCode(),
                user.getPhoneNumber(),
                user.getProfileImageUrl()
        );
    }

    @Transactional
    public void updateProfile(User user, ProfileUpdateRequest request) {
        user.updateProfile(
                request.name(),
                request.birthDate(),
                request.countryCode(),
                request.phoneNumber()
        );

        userRepository.save(user);
    }

}
