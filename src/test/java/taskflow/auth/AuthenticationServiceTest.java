package taskflow.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.transaction.annotation.Transactional;
import taskflow.infrastructure.security.JwtService;
import taskflow.user.CreateUserRequest;
import taskflow.user.UserService;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class AuthenticationServiceTest {
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserService userService;

    @Test
    void shouldReturnTokenOnValidCredentials() {
        CreateUserRequest userRequest = new CreateUserRequest("test@test.ru", "test123", "password123");
        userService.create(userRequest);

        LoginRequest request = new LoginRequest("test@test.ru", "password123");
        LoginResponse response = authenticationService.login(request);

        assertThat(jwtService.extractUsername(response.token())).isEqualTo("test@test.ru");
    }

    @Test
    void shouldThrowBadCredentialsOnInvalidCredentials() {
        LoginRequest request = new LoginRequest("hashtest@test.ru", "invalidPassword");

        assertThrows(BadCredentialsException.class, () -> authenticationService.login(request));
    }
}
