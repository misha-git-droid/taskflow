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
        userDetails = User.withUsername("test@test.ru")
                .password("password")
                .authorities(List.of())
                .build();

        jwtService = new JwtService();
    }

    @Test
    void shouldGenerateAndExtractUsername() {
        ReflectionTestUtils.setField(jwtService, "secretKey", "11842a2bb92de9224888eba38e8c901edfbc7b49a807c4b4f2c4ed7e8e6230104911d811f11ba79583e060b9fdbbc9585dc464024ff4a3c3d621699ee8ff2fee");
        ReflectionTestUtils.setField(jwtService, "expirationMs", 86400000);

        String token = jwtService.generateToken(userDetails);

        assertThat(jwtService.extractUsername(token)).isEqualTo("test@test.ru");
    }

    @Test
    void shouldValidateToken() {
        ReflectionTestUtils.setField(jwtService, "secretKey", "11842a2bb92de9224888eba38e8c901edfbc7b49a807c4b4f2c4ed7e8e6230104911d811f11ba79583e060b9fdbbc9585dc464024ff4a3c3d621699ee8ff2fee");
        ReflectionTestUtils.setField(jwtService, "expirationMs", 86400000);
        String token = jwtService.generateToken(userDetails);

        assertThat(jwtService.isTokenValid(token, userDetails)).isTrue();
    }

    @Test
    void shouldInvalidateTokenForDifferentUser() {
        ReflectionTestUtils.setField(jwtService, "secretKey", "11842a2bb92de9224888eba38e8c901edfbc7b49a807c4b4f2c4ed7e8e6230104911d811f11ba79583e060b9fdbbc9585dc464024ff4a3c3d621699ee8ff2fee");
        ReflectionTestUtils.setField(jwtService, "expirationMs", 86400000);
        String token = jwtService.generateToken(userDetails);

        userDetails = User.withUsername("test2@test.ru")
                        .password("password")
                        .authorities(List.of())
                        .build();

        assertThat(jwtService.isTokenValid(token, userDetails)).isFalse();
    }

    @Test
    void shouldThrowOnExpiredToken() {
        ReflectionTestUtils.setField(jwtService, "secretKey", "11842a2bb92de9224888eba38e8c901edfbc7b49a807c4b4f2c4ed7e8e6230104911d811f11ba79583e060b9fdbbc9585dc464024ff4a3c3d621699ee8ff2fee");
        ReflectionTestUtils.setField(jwtService, "expirationMs", 100);

        String token = jwtService.generateToken(userDetails);

        assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenValid(token, userDetails));
    }
}
