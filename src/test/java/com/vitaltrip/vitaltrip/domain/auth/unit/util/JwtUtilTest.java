package com.vitaltrip.vitaltrip.domain.auth.unit.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.vitaltrip.vitaltrip.domain.auth.util.JwtUtil;
import com.vitaltrip.vitaltrip.domain.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private User testUser;

    @BeforeEach
    void setUp() {
        // JwtUtil 생성 (테스트용 설정)
        jwtUtil = new JwtUtil(
            "myTestSecretKeyForJWTTokenGeneration123456789",
            3600000L,  // 1시간
            604800000L // 7일
        );

        testUser = User.builder()
            .id(1L)
            .email("test@example.com")
            .name("홍길동")
            .countryCode("KR")
            .phoneNumber("+821012345678")
            .role(User.Role.USER)
            .build();
    }

    @Nested
    @DisplayName("Access Token 테스트")
    class AccessTokenTest {

        @Test
        @DisplayName("Access Token 생성 성공")
        void generateAccessToken_Success() {
            // when
            String token = jwtUtil.generateAccessToken(testUser);

            // then
            assertThat(token).isNotNull();
            assertThat(token).isNotEmpty();
            assertThat(token.split("\\.")).hasSize(3);
        }

        @Test
        @DisplayName("Access Token에서 사용자 정보 추출")
        void extractUserInfoFromAccessToken() {
            // given
            String token = jwtUtil.generateAccessToken(testUser);

            // when
            Claims claims = jwtUtil.getClaims(token);

            // then
            assertThat(claims.getSubject()).isEqualTo("1");
            assertThat(claims.get("email", String.class)).isEqualTo("test@example.com");
            assertThat(claims.get("name", String.class)).isEqualTo("홍길동");
            assertThat(claims.get("countryCode", String.class)).isEqualTo("KR");
            assertThat(claims.get("phoneNumber", String.class)).isEqualTo("+821012345678");
            assertThat(claims.get("role", String.class)).isEqualTo("USER");
            assertThat(claims.get("temp")).isNull(); // temp 클레임 없음
        }

        @Test
        @DisplayName("Access Token 검증 성공")
        void validateAccessToken_Success() {
            // given
            String token = jwtUtil.generateAccessToken(testUser);

            // when & then
            assertThat(jwtUtil.validateToken(token)).isTrue();
            assertThat(jwtUtil.isTokenExpired(token)).isFalse();
        }
    }

    @Nested
    @DisplayName("Refresh Token 테스트")
    class RefreshTokenTest {

        @Test
        @DisplayName("Refresh Token 생성 성공")
        void generateRefreshToken_Success() {
            // when
            String token = jwtUtil.generateRefreshToken(testUser);

            // then
            assertThat(token).isNotNull();
            assertThat(token).isNotEmpty();
        }

        @Test
        @DisplayName("Refresh Token 검증 성공")
        void validateRefreshToken_Success() {
            // given
            String token = jwtUtil.generateRefreshToken(testUser);

            // when & then
            assertThat(jwtUtil.validateToken(token)).isTrue();
        }
    }

    @Nested
    @DisplayName("Temp Token 테스트")
    class TempTokenTest {

        @Test
        @DisplayName("Temp Token 생성 성공")
        void generateTempToken_Success() {
            // when
            String token = jwtUtil.generateTempToken(testUser);

            // then
            assertThat(token).isNotNull();
            assertThat(token).isNotEmpty();
        }

        @Test
        @DisplayName("Temp Token에서 사용자 정보 추출")
        void extractUserInfoFromTempToken() {
            // given
            String token = jwtUtil.generateTempToken(testUser);

            // when
            Claims claims = jwtUtil.getClaims(token);

            // then
            assertThat(claims.getSubject()).isEqualTo("1");
            assertThat(claims.get("email", String.class)).isEqualTo("test@example.com");
            assertThat(claims.get("name", String.class)).isEqualTo("홍길동");
            assertThat(claims.get("phoneNumber", String.class)).isEqualTo("+821012345678");
            assertThat(claims.get("temp", Boolean.class)).isTrue();
            assertThat(claims.get("role", String.class)).isEqualTo("TEMP_USER");
        }

        @Test
        @DisplayName("Temp Token 식별 성공")
        void isTempToken_Success() {
            // given
            String tempToken = jwtUtil.generateTempToken(testUser);
            String accessToken = jwtUtil.generateAccessToken(testUser);

            // when & then
            assertThat(jwtUtil.isTempToken(tempToken)).isTrue();
            assertThat(jwtUtil.isTempToken(accessToken)).isFalse();
        }
    }

    @Nested
    @DisplayName("토큰 정보 추출 테스트")
    class TokenExtractionTest {

        @Test
        @DisplayName("사용자 ID 추출 성공")
        void getUserId_Success() {
            // given
            String token = jwtUtil.generateAccessToken(testUser);

            // when
            String userId = jwtUtil.getUserId(token);

            // then
            assertThat(userId).isEqualTo("1");
        }

        @Test
        @DisplayName("국가 코드 추출 성공")
        void getCountryCode_Success() {
            // given
            String token = jwtUtil.generateAccessToken(testUser);

            // when
            String countryCode = jwtUtil.getCountryCode(token);

            // then
            assertThat(countryCode).isEqualTo("KR");
        }

        @Test
        @DisplayName("전화번호 추출 성공")
        void getPhoneNumber_Success() {
            // given
            String token = jwtUtil.generateAccessToken(testUser);

            // when
            String phoneNumber = jwtUtil.getPhoneNumber(token);

            // then
            assertThat(phoneNumber).isEqualTo("+821012345678");
        }

        @Test
        @DisplayName("전화번호가 null인 경우 처리")
        void getPhoneNumber_WhenNull() {
            // given
            User userWithoutPhone = User.builder()
                .id(1L)
                .email("test@example.com")
                .name("홍길동")
                .countryCode("KR")
                .phoneNumber(null)
                .role(User.Role.USER)
                .build();

            String token = jwtUtil.generateAccessToken(userWithoutPhone);

            // when
            String phoneNumber = jwtUtil.getPhoneNumber(token);

            // then
            assertThat(phoneNumber).isNull();
        }
    }

    @Nested
    @DisplayName("토큰 검증 실패 테스트")
    class TokenValidationFailureTest {

        @Test
        @DisplayName("유효하지 않은 토큰 검증 실패")
        void validateToken_InvalidToken_ReturnsFalse() {
            // given
            String invalidToken = "invalid.jwt.token";

            // when & then
            assertThat(jwtUtil.validateToken(invalidToken)).isFalse();
        }

        @Test
        @DisplayName("만료된 토큰 검증 실패")
        void validateToken_ExpiredToken_ReturnsFalse() throws InterruptedException {
            // given - 매우 짧은 만료 시간으로 토큰 생성
            JwtUtil shortLivedJwtUtil = new JwtUtil(
                "myTestSecretKeyForJWTTokenGeneration123456789",
                1L,  // 1ms
                1L   // 1ms
            );

            String token = shortLivedJwtUtil.generateAccessToken(testUser);

            // 토큰이 만료될 때까지 대기
            Thread.sleep(10);

            // when & then
            assertThat(shortLivedJwtUtil.validateToken(token)).isFalse();
            assertThat(shortLivedJwtUtil.isTokenExpired(token)).isTrue();
        }

        @Test
        @DisplayName("잘못된 토큰으로 Claims 추출 시 예외 발생")
        void getClaims_InvalidToken_ThrowsException() {
            // given
            String invalidToken = "invalid.jwt.token";

            // when & then
            assertThatThrownBy(() -> jwtUtil.getClaims(invalidToken))
                .isInstanceOf(JwtException.class);
        }

        @Test
        @DisplayName("잘못된 토큰으로 사용자 ID 추출 시 예외 발생")
        void getUserId_InvalidToken_ThrowsException() {
            // given
            String invalidToken = "invalid.jwt.token";

            // when & then
            assertThatThrownBy(() -> jwtUtil.getUserId(invalidToken))
                .isInstanceOf(JwtException.class);
        }

        @Test
        @DisplayName("잘못된 토큰으로 Temp Token 확인 시 false 반환")
        void isTempToken_InvalidToken_ReturnsFalse() {
            // given
            String invalidToken = "invalid.jwt.token";

            // when & then
            assertThat(jwtUtil.isTempToken(invalidToken)).isFalse();
        }
    }

    @Nested
    @DisplayName("토큰 만료 시간 테스트")
    class TokenExpirationTest {

        @Test
        @DisplayName("토큰 만료 시간 확인")
        void checkTokenExpiration() {
            // given
            String token = jwtUtil.generateAccessToken(testUser);
            Claims claims = jwtUtil.getClaims(token);

            // when
            Date issuedAt = claims.getIssuedAt();
            Date expiration = claims.getExpiration();
            long timeDiff = expiration.getTime() - issuedAt.getTime();

            // then
            assertThat(timeDiff).isEqualTo(3600000L); // 1시간 (3600000ms)
        }

        @Test
        @DisplayName("Refresh Token 만료 시간 확인")
        void checkRefreshTokenExpiration() {
            // given
            String token = jwtUtil.generateRefreshToken(testUser);
            Claims claims = jwtUtil.getClaims(token);

            // when
            Date issuedAt = claims.getIssuedAt();
            Date expiration = claims.getExpiration();
            long timeDiff = expiration.getTime() - issuedAt.getTime();

            // then
            assertThat(timeDiff).isEqualTo(604800000L); // 7일 (604800000ms)
        }

        @Test
        @DisplayName("Temp Token 만료 시간 확인")
        void checkTempTokenExpiration() {
            // given
            String token = jwtUtil.generateTempToken(testUser);
            Claims claims = jwtUtil.getClaims(token);

            // when
            Date issuedAt = claims.getIssuedAt();
            Date expiration = claims.getExpiration();
            long timeDiff = expiration.getTime() - issuedAt.getTime();

            // then
            assertThat(timeDiff).isEqualTo(1800000L); // 30분 (1800000ms)
        }
    }
}
