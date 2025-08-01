package com.vitaltrip.vitaltrip.domain.auth.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.vitaltrip.vitaltrip.common.exception.CustomException;
import com.vitaltrip.vitaltrip.common.exception.ErrorType;
import com.vitaltrip.vitaltrip.domain.auth.dto.AuthDto;
import com.vitaltrip.vitaltrip.domain.auth.util.JwtUtil;
import com.vitaltrip.vitaltrip.domain.user.User;
import com.vitaltrip.vitaltrip.domain.user.repository.UserRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private AuthDto.SignUpRequest validSignUpRequest;
    private AuthDto.LoginRequest validLoginRequest;
    private User testUser;

    @BeforeEach
    void setUp() {
        validSignUpRequest = new AuthDto.SignUpRequest(
            "test@example.com",
            "홍길동",
            "Password123!",
            "Password123!",
            LocalDate.of(1990, 1, 1),
            "KR",
            "+821012345678"
        );

        validLoginRequest = new AuthDto.LoginRequest(
            "test@example.com",
            "Password123!"
        );

        testUser = User.builder()
            .id(1L)
            .email("test@example.com")
            .name("홍길동")
            .passwordHash("encodedPassword")
            .birthDate(LocalDate.of(1990, 1, 1))
            .countryCode("KR")
            .phoneNumber("+821012345678")
            .provider(User.AuthProvider.LOCAL)
            .role(User.Role.USER)
            .build();
    }

    @Nested
    @DisplayName("회원가입 테스트")
    class SignUpTest {

        @Test
        @DisplayName("정상적인 회원가입 요청시 성공한다")
        void signUp_Success() {
            // given
            given(userRepository.existsByEmail(validSignUpRequest.email())).willReturn(false);
            given(passwordEncoder.encode(validSignUpRequest.password())).willReturn(
                "encodedPassword");
            given(userRepository.save(any(User.class))).willReturn(testUser);

            // when & then
            assertThatNoException().isThrownBy(() -> authService.signUp(validSignUpRequest));

            // verify
            then(userRepository).should().existsByEmail(validSignUpRequest.email());
            then(passwordEncoder).should().encode(validSignUpRequest.password());
            then(userRepository).should().save(any(User.class));
        }

        @Test
        @DisplayName("이미 존재하는 이메일로 회원가입시 예외가 발생한다")
        void signUp_DuplicateEmail_ThrowsException() {
            // given
            given(userRepository.existsByEmail(validSignUpRequest.email())).willReturn(true);

            // when & then
            assertThatThrownBy(() -> authService.signUp(validSignUpRequest))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.DUPLICATE_EMAIL);

            // verify
            then(userRepository).should().existsByEmail(validSignUpRequest.email());
            then(passwordEncoder).should(never()).encode(any());
            then(userRepository).should(never()).save(any(User.class));
        }

        @Test
        @DisplayName("비밀번호가 일치하지 않으면 예외가 발생한다")
        void signUp_PasswordMismatch_ThrowsException() {
            // given
            AuthDto.SignUpRequest mismatchRequest = new AuthDto.SignUpRequest(
                "test@example.com",
                "홍길동",
                "Password123!",
                "DifferentPassword123!",  // 다른 비밀번호
                LocalDate.of(1990, 1, 1),
                "KR",
                "+821012345678"
            );
            given(userRepository.existsByEmail(mismatchRequest.email())).willReturn(false);

            // when & then
            assertThatThrownBy(() -> authService.signUp(mismatchRequest))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.INVALID_REQUEST)
                .hasMessageContaining("비밀번호와 비밀번호 확인이 일치하지 않습니다");

            // verify
            then(userRepository).should().existsByEmail(mismatchRequest.email());
            then(passwordEncoder).should(never()).encode(any());
            then(userRepository).should(never()).save(any(User.class));
        }

        @Test
        @DisplayName("사용자 정보가 올바르게 저장된다")
        void signUp_UserDataSavedCorrectly() {
            // given
            given(userRepository.existsByEmail(validSignUpRequest.email())).willReturn(false);
            given(passwordEncoder.encode(validSignUpRequest.password())).willReturn(
                "encodedPassword");

            // when
            authService.signUp(validSignUpRequest);

            // then
            then(userRepository).should().save(argThat(user ->
                user.getEmail().equals(validSignUpRequest.email()) &&
                    user.getName().equals(validSignUpRequest.name()) &&
                    user.getPasswordHash().equals("encodedPassword") &&
                    user.getBirthDate().equals(validSignUpRequest.birthDate()) &&
                    user.getCountryCode().equals(validSignUpRequest.countryCode()) &&
                    user.getPhoneNumber().equals(validSignUpRequest.phoneNumber()) &&
                    user.getProvider() == User.AuthProvider.LOCAL &&
                    user.getRole() == User.Role.USER
            ));
        }
    }

    @Nested
    @DisplayName("로그인 테스트")
    class LoginTest {

        @Test
        @DisplayName("로그인 성공")
        void login_Success() {
            // given
            given(userRepository.findByEmail(anyString())).willReturn(Optional.of(testUser));
            given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);
            given(jwtUtil.generateAccessToken(any(User.class))).willReturn("accessToken");
            given(jwtUtil.generateRefreshToken(any(User.class))).willReturn("refreshToken");

            // when
            AuthDto.AuthResponse response = authService.login(validLoginRequest);

            // then
            assertThat(response).isNotNull();
            assertThat(response.accessToken()).isEqualTo("accessToken");
            assertThat(response.refreshToken()).isEqualTo("refreshToken");
            assertThat(response.user().email()).isEqualTo("test@example.com");

            verify(userRepository).findByEmail("test@example.com");
            verify(passwordEncoder).matches("Password123!", "encodedPassword");
        }

        @Test
        @DisplayName("존재하지 않는 이메일로 로그인 시 예외 발생")
        void login_EmailNotFound_ThrowsException() {
            // given
            given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> authService.login(validLoginRequest))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.RESOURCE_NOT_FOUND)
                .hasMessageContaining("등록되지 않은 이메일입니다");

            verify(userRepository).findByEmail("test@example.com");
            verify(passwordEncoder, never()).matches(anyString(), anyString());
        }

        @Test
        @DisplayName("소셜 로그인 사용자가 일반 로그인 시도 시 예외 발생")
        void login_SocialUser_ThrowsException() {
            // given
            User socialUser = User.builder()
                .email("test@example.com")
                .provider(User.AuthProvider.GOOGLE)
                .build();

            given(userRepository.findByEmail(anyString())).willReturn(Optional.of(socialUser));

            // when & then
            assertThatThrownBy(() -> authService.login(validLoginRequest))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.INVALID_REQUEST)
                .hasMessageContaining("소셜 로그인 사용자는 해당 방식으로 로그인해주세요");
        }

        @Test
        @DisplayName("비밀번호 불일치 시 예외 발생")
        void login_WrongPassword_ThrowsException() {
            // given
            given(userRepository.findByEmail(anyString())).willReturn(Optional.of(testUser));
            given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);

            // when & then
            assertThatThrownBy(() -> authService.login(validLoginRequest))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.UNAUTHORIZED)
                .hasMessageContaining("비밀번호가 일치하지 않습니다");
        }
    }

    @Nested
    @DisplayName("토큰 갱신 테스트")
    class RefreshTokenTest {

        @Test
        @DisplayName("토큰 갱신 성공")
        void refreshToken_Success() {
            // given
            AuthDto.TokenRefreshRequest request = new AuthDto.TokenRefreshRequest("refreshToken");

            given(jwtUtil.validateToken(anyString())).willReturn(true);
            given(jwtUtil.getUserId(anyString())).willReturn("1");
            given(userRepository.findById(anyLong())).willReturn(Optional.of(testUser));
            given(jwtUtil.generateAccessToken(any(User.class))).willReturn("newAccessToken");

            // when
            AuthDto.TokenResponse response = authService.refreshToken(request);

            // then
            assertThat(response).isNotNull();
            assertThat(response.accessToken()).isEqualTo("newAccessToken");

            verify(jwtUtil).validateToken("refreshToken");
            verify(jwtUtil).getUserId("refreshToken");
            verify(userRepository).findById(1L);
        }

        @Test
        @DisplayName("유효하지 않은 리프레시 토큰으로 갱신 시 예외 발생")
        void refreshToken_InvalidToken_ThrowsException() {
            // given
            AuthDto.TokenRefreshRequest request = new AuthDto.TokenRefreshRequest("invalidToken");
            given(jwtUtil.validateToken(anyString())).willReturn(false);

            // when & then
            assertThatThrownBy(() -> authService.refreshToken(request))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.UNAUTHORIZED)
                .hasMessageContaining("유효하지 않은 리프레시 토큰입니다");
        }

        @Test
        @DisplayName("존재하지 않는 사용자 ID로 토큰 갱신 시 예외 발생")
        void refreshToken_UserNotFound_ThrowsException() {
            // given
            AuthDto.TokenRefreshRequest request = new AuthDto.TokenRefreshRequest("refreshToken");

            given(jwtUtil.validateToken(anyString())).willReturn(true);
            given(jwtUtil.getUserId(anyString())).willReturn("999");
            given(userRepository.findById(anyLong())).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> authService.refreshToken(request))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.RESOURCE_NOT_FOUND)
                .hasMessageContaining("사용자를 찾을 수 없습니다");
        }
    }

    @Nested
    @DisplayName("비밀번호 변경 테스트")
    class ChangePasswordTest {

        @Test
        @DisplayName("비밀번호 변경 성공")
        void changePassword_Success() {
            // given
            AuthDto.PasswordChangeRequest request = new AuthDto.PasswordChangeRequest(
                "oldPassword",
                "NewPassword123!",
                "NewPassword123!"
            );

            given(passwordEncoder.matches("oldPassword", "encodedPassword")).willReturn(true);
            given(passwordEncoder.encode("NewPassword123!")).willReturn("newEncodedPassword");

            // when
            authService.changePassword(testUser, request);

            // then
            verify(passwordEncoder).matches("oldPassword", "encodedPassword");
            verify(passwordEncoder).encode("NewPassword123!");
        }

        @Test
        @DisplayName("소셜 로그인 사용자 비밀번호 변경 시 예외 발생")
        void changePassword_SocialUser_ThrowsException() {
            // given
            User socialUser = User.builder()
                .provider(User.AuthProvider.GOOGLE)
                .build();

            AuthDto.PasswordChangeRequest request = new AuthDto.PasswordChangeRequest(
                "oldPassword",
                "NewPassword123!",
                "NewPassword123!"
            );

            // when & then
            assertThatThrownBy(() -> authService.changePassword(socialUser, request))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.INVALID_REQUEST)
                .hasMessageContaining("소셜 로그인 사용자는 비밀번호를 변경할 수 없습니다");
        }

        @Test
        @DisplayName("현재 비밀번호 불일치 시 예외 발생")
        void changePassword_WrongCurrentPassword_ThrowsException() {
            // given
            AuthDto.PasswordChangeRequest request = new AuthDto.PasswordChangeRequest(
                "wrongPassword",
                "NewPassword123!",
                "NewPassword123!"
            );

            given(passwordEncoder.matches("wrongPassword", "encodedPassword")).willReturn(false);

            // when & then
            assertThatThrownBy(() -> authService.changePassword(testUser, request))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.UNAUTHORIZED)
                .hasMessageContaining("현재 비밀번호가 일치하지 않습니다");
        }

        @Test
        @DisplayName("새 비밀번호 확인 불일치 시 예외 발생")
        void changePassword_NewPasswordMismatch_ThrowsException() {
            // given
            AuthDto.PasswordChangeRequest request = new AuthDto.PasswordChangeRequest(
                "oldPassword",
                "NewPassword123!",
                "DifferentPassword!"
            );

            given(passwordEncoder.matches("oldPassword", "encodedPassword")).willReturn(true);

            // when & then
            assertThatThrownBy(() -> authService.changePassword(testUser, request))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.INVALID_REQUEST)
                .hasMessageContaining("새 비밀번호와 비밀번호 확인이 일치하지 않습니다");
        }
    }

}
