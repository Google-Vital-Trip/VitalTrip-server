package com.vitaltrip.vitaltrip.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;

import com.vitaltrip.vitaltrip.domain.user.User;
import com.vitaltrip.vitaltrip.domain.user.dto.ProfileUpdateRequest;
import com.vitaltrip.vitaltrip.domain.user.dto.UserInfoResponse;
import com.vitaltrip.vitaltrip.domain.user.repository.UserRepository;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 단위 테스트")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .name("홍길동")
                .birthDate(LocalDate.of(1990, 1, 1))
                .countryCode("KR")
                .phoneNumber("+821012345678")
                .profileImageUrl("http://example.com/profile.jpg")
                .provider(User.AuthProvider.LOCAL)
                .role(User.Role.USER)
                .build();
    }

    @Test
    @DisplayName("사용자 정보 조회 성공")
    void getUserInfo_Success() {
        // when
        UserInfoResponse result = userService.getUserInfo(testUser);

        // then
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.email()).isEqualTo("test@example.com");
        assertThat(result.name()).isEqualTo("홍길동");
        assertThat(result.birthDate()).isEqualTo(LocalDate.of(1990, 1, 1));
        assertThat(result.countryCode()).isEqualTo("KR");
        assertThat(result.phoneNumber()).isEqualTo("+821012345678");
        assertThat(result.profileImageUrl()).isEqualTo("http://example.com/profile.jpg");
    }

    @Test
    @DisplayName("프로필 업데이트 성공")
    void updateProfile_Success() {
        // given
        ProfileUpdateRequest request = new ProfileUpdateRequest(
                "김길동",
                LocalDate.of(1992, 3, 15),
                "US",
                "+12345678901"
        );

        // when
        userService.updateProfile(testUser, request);

        // then
        then(userRepository).should().save(testUser);
    }

    @Test
    @DisplayName("프로필 업데이트 시 사용자 정보 변경 확인")
    void updateProfile_UserInfoChanged() {
        // given
        ProfileUpdateRequest request = new ProfileUpdateRequest(
                "김길동",
                LocalDate.of(1992, 3, 15),
                "US",
                "+12345678901"
        );

        // when
        userService.updateProfile(testUser, request);

        verify(userRepository).save(testUser);
    }
}
