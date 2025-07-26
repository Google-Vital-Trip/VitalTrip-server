package com.vitaltrip.vitaltrip.domain.auth.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vitaltrip.vitaltrip.domain.auth.dto.AuthDto;
import com.vitaltrip.vitaltrip.domain.user.User;
import com.vitaltrip.vitaltrip.domain.user.repository.UserRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("인증 통합 테스트")
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private AuthDto.SignUpRequest validSignUpRequest;
    private AuthDto.LoginRequest validLoginRequest;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

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
    }

    @Nested
    @DisplayName("회원가입 통합 테스트")
    class SignUpIntegrationTest {

        @Test
        @DisplayName("회원가입 성공 - 모든 필드 포함")
        void signUp_Success_WithAllFields() throws Exception {
            // when
            ResultActions result = mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validSignUpRequest)));

            // then
            result.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("성공"))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists())
                .andExpect(jsonPath("$.data.user.id").exists())
                .andExpect(jsonPath("$.data.user.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.user.name").value("홍길동"))
                .andExpect(jsonPath("$.data.user.birthDate").value("1990-01-01"))
                .andExpect(jsonPath("$.data.user.countryCode").value("KR"))
                .andExpect(jsonPath("$.data.user.phoneNumber").value("+821012345678"))
                .andExpect(jsonPath("$.errorCode").doesNotExist());

            // 데이터베이스 검증
            assertThat(userRepository.existsByEmail("test@example.com")).isTrue();
            User savedUser = userRepository.findByEmail("test@example.com").orElseThrow();
            assertThat(savedUser.getName()).isEqualTo("홍길동");
            assertThat(savedUser.getProvider()).isEqualTo(User.AuthProvider.LOCAL);
            assertThat(savedUser.getRole()).isEqualTo(User.Role.USER);
        }

        @Test
        @DisplayName("회원가입 성공 - 전화번호 없음")
        void signUp_Success_WithoutPhoneNumber() throws Exception {
            // given
            AuthDto.SignUpRequest requestWithoutPhone = new AuthDto.SignUpRequest(
                "test2@example.com",
                "김철수",
                "Password123!",
                "Password123!",
                LocalDate.of(1995, 5, 15),
                "US",
                null
            );

            // when
            ResultActions result = mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestWithoutPhone)));

            // then
            result.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.user.email").value("test2@example.com"))
                .andExpect(jsonPath("$.data.user.name").value("김철수"))
                .andExpect(jsonPath("$.data.user.countryCode").value("US"))
                .andExpect(jsonPath("$.data.user.phoneNumber").isEmpty());
        }

        @Test
        @DisplayName("회원가입 실패 - 이메일 중복")
        void signUp_Fail_DuplicateEmail() throws Exception {
            // given - 먼저 사용자 등록
            mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validSignUpRequest)));

            // when - 동일한 이메일로 재등록 시도
            ResultActions result = mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validSignUpRequest)));

            // then
            result.andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("이미 사용 중인 이메일입니다."))
                .andExpect(jsonPath("$.errorCode").value("DUPLICATE_EMAIL"))
                .andExpect(jsonPath("$.data").doesNotExist());
        }

        @Test
        @DisplayName("회원가입 실패 - 비밀번호 불일치")
        void signUp_Fail_PasswordMismatch() throws Exception {
            // given
            AuthDto.SignUpRequest mismatchRequest = new AuthDto.SignUpRequest(
                "test@example.com",
                "홍길동",
                "Password123!",
                "DifferentPassword!",
                LocalDate.of(1990, 1, 1),
                "KR",
                "+821012345678"
            );

            // when
            ResultActions result = mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mismatchRequest)));

            // then
            result.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("비밀번호와 비밀번호 확인이 일치하지 않습니다"))
                .andExpect(jsonPath("$.errorCode").value("INVALID_REQUEST"));
        }

        @Test
        @DisplayName("회원가입 실패 - 유효성 검증 오류")
        void signUp_Fail_ValidationErrors() throws Exception {
            // given
            AuthDto.SignUpRequest invalidRequest = new AuthDto.SignUpRequest(
                "invalid-email",           // 잘못된 이메일 형식
                "a",                      // 너무 짧은 이름
                "weak",                   // 약한 비밀번호
                "weak",
                LocalDate.now().plusDays(1), // 미래 날짜
                "INVALID",                // 잘못된 국가 코드
                "invalid-phone"           // 잘못된 전화번호 형식
            );

            // when
            ResultActions result = mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)));

            // then
            result.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"));
        }
    }

    @Nested
    @DisplayName("로그인 통합 테스트")
    class LoginIntegrationTest {

        @BeforeEach
        void setUpUser() throws Exception {
            // 테스트용 사용자 등록
            mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validSignUpRequest)));
        }

        @Test
        @DisplayName("로그인 성공")
        void login_Success() throws Exception {
            // when
            ResultActions result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validLoginRequest)));

            // then
            result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("성공"))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists())
                .andExpect(jsonPath("$.data.user.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.user.name").value("홍길동"))
                .andExpect(jsonPath("$.errorCode").doesNotExist());
        }

        @Test
        @DisplayName("로그인 실패 - 존재하지 않는 이메일")
        void login_Fail_EmailNotFound() throws Exception {
            // given
            AuthDto.LoginRequest nonExistentRequest = new AuthDto.LoginRequest(
                "nonexistent@example.com",
                "Password123!"
            );

            // when
            ResultActions result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nonExistentRequest)));

            // then
            result.andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("등록되지 않은 이메일입니다"))
                .andExpect(jsonPath("$.errorCode").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.data").doesNotExist());
        }

        @Test
        @DisplayName("로그인 실패 - 잘못된 비밀번호")
        void login_Fail_WrongPassword() throws Exception {
            // given
            AuthDto.LoginRequest wrongPasswordRequest = new AuthDto.LoginRequest(
                "test@example.com",
                "WrongPassword123!"
            );

            // when
            ResultActions result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(wrongPasswordRequest)));

            // then
            result.andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("비밀번호가 일치하지 않습니다"))
                .andExpect(jsonPath("$.errorCode").value("UNAUTHORIZED"));
        }

        @Test
        @DisplayName("로그인 실패 - 소셜 로그인 사용자")
        void login_Fail_SocialUser() throws Exception {
            // given - 소셜 로그인 사용자 생성
            User socialUser = User.builder()
                .email("social@example.com")
                .name("소셜사용자")
                .provider(User.AuthProvider.GOOGLE)
                .providerId("google123")
                .role(User.Role.USER)
                .build();
            userRepository.save(socialUser);

            AuthDto.LoginRequest socialLoginRequest = new AuthDto.LoginRequest(
                "social@example.com",
                "Password123!"
            );

            // when
            ResultActions result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(socialLoginRequest)));

            // then
            result.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("소셜 로그인 사용자는 해당 방식으로 로그인해주세요"))
                .andExpect(jsonPath("$.errorCode").value("INVALID_REQUEST"));
        }

        @Test
        @DisplayName("로그인 실패 - 유효성 검증 오류")
        void login_Fail_ValidationErrors() throws Exception {
            // given
            AuthDto.LoginRequest invalidRequest = new AuthDto.LoginRequest(
                "invalid-email",  // 잘못된 이메일 형식
                ""               // 빈 비밀번호
            );

            // when
            ResultActions result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)));

            // then
            result.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"));
        }
    }

    @Nested
    @DisplayName("토큰 갱신 통합 테스트")
    class RefreshTokenIntegrationTest {

        private String refreshToken;

        @BeforeEach
        void setUpUserAndToken() throws Exception {
            // 회원가입
            ResultActions signupResult = mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validSignUpRequest)));

            // 리프레시 토큰 추출
            String response = signupResult.andReturn().getResponse().getContentAsString();
            refreshToken = objectMapper.readTree(response)
                .path("data")
                .path("refreshToken")
                .asText();
        }

        @Test
        @DisplayName("토큰 갱신 성공")
        void refreshToken_Success() throws Exception {
            // given
            AuthDto.TokenRefreshRequest request = new AuthDto.TokenRefreshRequest(refreshToken);

            // when
            ResultActions result = mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

            // then
            result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("성공"))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.errorCode").doesNotExist());
        }

        @Test
        @DisplayName("토큰 갱신 실패 - 유효하지 않은 토큰")
        void refreshToken_Fail_InvalidToken() throws Exception {
            // given
            AuthDto.TokenRefreshRequest request = new AuthDto.TokenRefreshRequest(
                "invalid.jwt.token");

            // when
            ResultActions result = mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

            // then
            result.andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("유효하지 않은 리프레시 토큰입니다"))
                .andExpect(jsonPath("$.errorCode").value("UNAUTHORIZED"));
        }
    }

    @Nested
    @DisplayName("인증이 필요한 엔드포인트 테스트")
    class AuthenticatedEndpointTest {

        private String accessToken;

        @BeforeEach
        void setUpUserAndToken() throws Exception {
            // 회원가입 후 토큰 추출
            ResultActions signupResult = mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validSignUpRequest)));

            String response = signupResult.andReturn().getResponse().getContentAsString();
            accessToken = objectMapper.readTree(response)
                .path("data")
                .path("accessToken")
                .asText();
        }

        @Test
        @DisplayName("내 정보 조회 성공")
        void getMyInfo_Success() throws Exception {
            // when
            ResultActions result = mockMvc.perform(get("/api/auth/me")
                .header("Authorization", "Bearer " + accessToken));

            // then
            result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.name").value("홍길동"))
                .andExpect(jsonPath("$.data.countryCode").value("KR"));
        }

        @Test
        @DisplayName("인증 없이 내 정보 조회 실패")
        void getMyInfo_Fail_NoAuthentication() throws Exception {
            // when
            ResultActions result = mockMvc.perform(get("/api/auth/me"));

            // then
            result.andDo(print())
                .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("유효하지 않은 토큰으로 내 정보 조회 실패")
        void getMyInfo_Fail_InvalidToken() throws Exception {
            // when
            ResultActions result = mockMvc.perform(get("/api/auth/me")
                .header("Authorization", "Bearer invalid.jwt.token"));

            // then
            result.andDo(print())
                .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("프로필 업데이트 성공")
        void updateProfile_Success() throws Exception {
            // given
            AuthDto.ProfileUpdateRequest updateRequest = new AuthDto.ProfileUpdateRequest(
                "김길동",
                LocalDate.of(1992, 3, 15),
                "US",
                "+12345678901"
            );

            // when
            ResultActions result = mockMvc.perform(put("/api/auth/profile")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)));

            // then
            result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("프로필이 업데이트되었습니다"));

            // 데이터베이스 검증
            User updatedUser = userRepository.findByEmail("test@example.com").orElseThrow();
            assertThat(updatedUser.getName()).isEqualTo("김길동");
            assertThat(updatedUser.getCountryCode()).isEqualTo("US");
            assertThat(updatedUser.getPhoneNumber()).isEqualTo("+12345678901");
        }

        @Test
        @DisplayName("비밀번호 변경 성공")
        void changePassword_Success() throws Exception {
            // given
            AuthDto.PasswordChangeRequest changeRequest = new AuthDto.PasswordChangeRequest(
                "Password123!",
                "NewPassword123!",
                "NewPassword123!"
            );

            // when
            ResultActions result = mockMvc.perform(put("/api/auth/password")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(changeRequest)));

            // then
            result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("비밀번호가 변경되었습니다"));

            // 새 비밀번호로 로그인 가능한지 검증
            AuthDto.LoginRequest newLoginRequest = new AuthDto.LoginRequest(
                "test@example.com",
                "NewPassword123!"
            );

            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newLoginRequest)))
                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("로그아웃 성공")
        void logout_Success() throws Exception {
            // when
            ResultActions result = mockMvc.perform(post("/api/auth/logout")
                .header("Authorization", "Bearer " + accessToken));

            // then
            result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("로그아웃되었습니다. 클라이언트에서 토큰을 삭제해주세요."));
        }
    }

    @Nested
    @DisplayName("회원가입과 로그인 플로우 통합 테스트")
    class SignUpLoginFlowTest {

        @Test
        @DisplayName("전체 플로우 테스트: 회원가입 → 로그인 → 내 정보 조회 → 프로필 수정")
        void fullAuthFlow_Success() throws Exception {
            // 1. 회원가입
            ResultActions signupResult = mockMvc.perform(post("/api/auth/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validSignUpRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.user.email").value("test@example.com"));

            // 2. 로그인
            ResultActions loginResult = mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validLoginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").exists());

            // 액세스 토큰 추출
            String loginResponse = loginResult.andReturn().getResponse().getContentAsString();
            String accessToken = objectMapper.readTree(loginResponse)
                .path("data")
                .path("accessToken")
                .asText();

            // 3. 내 정보 조회
            mockMvc.perform(get("/api/auth/me")
                    .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("홍길동"));

            // 4. 프로필 수정
            AuthDto.ProfileUpdateRequest updateRequest = new AuthDto.ProfileUpdateRequest(
                "홍길동2",
                LocalDate.of(1990, 1, 1),
                "KR",
                "+821012345679"
            );

            mockMvc.perform(put("/api/auth/profile")
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());

            // 5. 변경된 정보 확인
            mockMvc.perform(get("/api/auth/me")
                    .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("홍길동2"))
                .andExpect(jsonPath("$.data.phoneNumber").value("+821012345679"));
        }
    }
}
