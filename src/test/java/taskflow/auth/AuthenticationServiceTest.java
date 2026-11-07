package taskflow.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import taskflow.infrastructure.security.JwtService;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class AuthenticationServiceTest {
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private JwtService jwtService;

    @Test
    void shouldReturnTokenOnValidCredentials() {
        LoginRequest request = new LoginRequest("hashtest@test.ru", "password123");
        LoginResponse response = authenticationService.login(request);

        assertThat(jwtService.extractUsername(response.token())).isEqualTo("hashtest@test.ru");
    }

    @Test
    void shouldThrowBadCredentialsOnInvalidCredentials() {
        LoginRequest request = new LoginRequest("hashtest@test.ru", "invalidPassword");

        assertThrows(BadCredentialsException.class, () -> authenticationService.login(request));
    }
}
