package taskflow.security;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;
import taskflow.infrastructure.security.JwtService;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

public class JwtServiceTest {
    private JwtService jwtService;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", "DPzK/pSLQZ2fM5GlC42p6IOYo/8TJ6aMNQblLzbhZaE=");
        ReflectionTestUtils.setField(jwtService, "expirationMs", 86400000);
        userDetails = User.withUsername("test@test.ru")
                .password("password")
                .authorities(List.of())
                .build();
    }

    @Test
    void shouldGenerateAndExtractUsername() {
        String token = jwtService.generateToken(userDetails);

        assertThat(jwtService.extractUsername(token)).isEqualTo("test@test.ru");
    }

    @Test
    void shouldValidateToken() {
        String token = jwtService.generateToken(userDetails);

        assertThat(jwtService.isTokenValid(token, userDetails)).isTrue();
    }

    @Test
    void shouldInvalidateTokenForDifferentUser() {
        String token = jwtService.generateToken(userDetails);

        userDetails = User.withUsername("test2@test.ru")
                        .password("password")
                        .authorities(List.of())
                        .build();

        assertThat(jwtService.isTokenValid(token, userDetails)).isFalse();
    }

    @Test
    void shouldReturnFalseOnExpiredToken() {
        ReflectionTestUtils.setField(jwtService, "expirationMs", 100);

        String token = jwtService.generateToken(userDetails);

        assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenValid(token, userDetails));
    }
}
