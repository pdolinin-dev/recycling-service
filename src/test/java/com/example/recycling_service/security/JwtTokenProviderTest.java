package com.example.recycling_service.security;

import com.example.recycling_service.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    @Mock
    UserService userService;

    @InjectMocks
    JwtTokenProvider jwtTokenProvider;

    private static final String TEST_SECRET =
            "dGVzdFNlY3JldEtleVdoaWNoSXNMb25nRW5vdWdoRm9ySFMyNTY=";
    private static final int TEST_EXPIRATION = 86400000;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationInMs", TEST_EXPIRATION);
    }

    @Test
    @DisplayName("Генерация токена - валидация - извлечение логина")
    void generateToken_thenValidateTokenAndExtractUsername() {
        String token = jwtTokenProvider.generateToken("test_login");

        assertThat(jwtTokenProvider.validateToken(token)).isTrue();
        assertThat(jwtTokenProvider.getUsernameFromToken(token)).isEqualTo("test_login");
    }

    @Test
    @DisplayName("Протухщий токен - validateToken возвращает False")
    void validateToken_expiredToken_returnsFalse() {
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationInMs", -1000);

        String expiredToken = jwtTokenProvider.generateToken("test_login");

        assertThat(jwtTokenProvider.validateToken(expiredToken)).isFalse();
    }

    @Test
    @DisplayName("Битая подпись - validateToken возвращает False")
    void validateToken_tamperedSignature_returnsFalse() {
        String token = jwtTokenProvider.generateToken("test_login");

        String tampered = token.substring(0, token.length() - 5) + "XXXXX";

        assertThat(jwtTokenProvider.validateToken(tampered)).isFalse();
    }

    @Test
    @DisplayName("Некорректный токен - validateToken возвращает False")
    void validateToken_garbageToken_returnsFalse() {
        assertThat(jwtTokenProvider.validateToken("not.a.jwt.token")).isFalse();
    }

    void getAuthentication_success() {

    }

}