package taskflow.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class UserDetailsServiceImplTest {
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private UserService userService;

    @Test
    void shouldLoadUserByEmailIfUserExists() {
        userService.create(new CreateUserRequest("test@test.ru", "testuser", "password123"));
        UserDetails userDetails = userDetailsService.loadUserByUsername("test@test.ru");

        assertThat(userDetails.getUsername()).isEqualTo("test@test.ru");
    }

    @Test
    void shouldThrowUsernameNotFoundException() {
        UsernameNotFoundException ex = assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername("test@test.ru"));
        assertThat(ex.getMessage()).isEqualTo("User not found with email: test@test.ru");
    }
}
