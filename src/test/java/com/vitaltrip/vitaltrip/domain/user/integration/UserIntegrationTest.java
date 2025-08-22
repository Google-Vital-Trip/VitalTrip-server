package com.vitaltrip.vitaltrip.domain.user.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vitaltrip.vitaltrip.domain.user.User;
import com.vitaltrip.vitaltrip.domain.user.dto.ProfileUpdateRequest;
import com.vitaltrip.vitaltrip.domain.user.repository.UserRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("UserController 통합 테스트")
class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        testUser = User.builder()
                .email("test@example.com")
                .name("홍길동")
                .birthDate(LocalDate.of(1990, 1, 1))
                .countryCode("KR")
                .phoneNumber("+821012345678")
                .provider(User.AuthProvider.LOCAL)
                .role(User.Role.USER)
                .build();

        testUser = userRepository.save(testUser);
    }

    @Test
    @WithMockUser
    @DisplayName("프로필 조회 성공")
    void getProfile_Success() throws Exception {
        // when & then
        mockMvc.perform(get("/api/user/profile")
                        .principal(() -> testUser.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("성공"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.name").value("홍길동"))
                .andExpect(jsonPath("$.data.countryCode").value("KR"));
    }

    @Test
    @DisplayName("인증 없이 프로필 조회 실패")
    void getProfile_Fail_NoAuth() throws Exception {
        // when & then
        mockMvc.perform(get("/api/user/profile"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("프로필 수정 성공")
    void updateProfile_Success() throws Exception {
        // given
        ProfileUpdateRequest request = new ProfileUpdateRequest(
                "김길동",
                LocalDate.of(1992, 3, 15),
                "US",
                "+12345678901"
        );

        // when
        mockMvc.perform(put("/api/user/profile")
                        .principal(() -> testUser.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("프로필이 업데이트되었습니다"));

        // then - DB 검증
        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertThat(updatedUser.getName()).isEqualTo("김길동");
        assertThat(updatedUser.getCountryCode()).isEqualTo("US");
        assertThat(updatedUser.getPhoneNumber()).isEqualTo("+12345678901");
    }

    @Test
    @WithMockUser
    @DisplayName("프로필 수정 실패 - 유효성 검증 오류")
    void updateProfile_Fail_Validation() throws Exception {
        // given - 잘못된 요청
        ProfileUpdateRequest request = new ProfileUpdateRequest(
                "", // 빈 이름
                LocalDate.now().plusDays(1), // 미래 날짜
                "INVALID", // 잘못된 국가 코드
                "invalid-phone" // 잘못된 전화번호
        );

        // when & then
        mockMvc.perform(put("/api/user/profile")
                        .principal(() -> testUser.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"));
    }

    @Test
    @DisplayName("인증 없이 프로필 수정 실패")
    void updateProfile_Fail_NoAuth() throws Exception {
        // given
        ProfileUpdateRequest request = new ProfileUpdateRequest(
                "김길동",
                LocalDate.of(1992, 3, 15),
                "US",
                "+12345678901"
        );

        // when & then
        mockMvc.perform(put("/api/user/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
