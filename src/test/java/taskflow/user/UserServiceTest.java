package taskflow.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import taskflow.infrastructure.exception.EmailAlreadyExistsException;
import taskflow.infrastructure.exception.UserNotFoundException;
import taskflow.infrastructure.exception.UsernameAlreadyExistsException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class UserServiceTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @Test
    void shouldCreateUserAndReturnUserResponse() {
        CreateUserRequest request = new CreateUserRequest("test1@example.com", "testuser", "password123");
        UserResponse response = userService.create(request);

        assertThat(userRepository.existsByEmail("test1@example.com")).isTrue();
        assertThat(request.username()).isEqualTo(response.username());
    }

    @Test
    void shouldThrowExceptionIfDuplicateEmail() {
        CreateUserRequest request = new CreateUserRequest("test1@example.com", "testuser", "password123");
        CreateUserRequest dup = new CreateUserRequest("test1@example.com", "testuserNew", "password123");
        userService.create(request);

        EmailAlreadyExistsException ex = assertThrows(EmailAlreadyExistsException.class, () -> userService.create(dup));
        assertThat(ex.getMessage()).isEqualTo("Email already exists");
    }

    @Test
    void shouldThrowExceptionIfDuplicateUsername() {
        CreateUserRequest request = new CreateUserRequest("test1@example.com", "testuser", "password123");
        CreateUserRequest dup = new CreateUserRequest("testNew@example.com", "testuser", "password123");
        userService.create(request);

        UsernameAlreadyExistsException ex = assertThrows(UsernameAlreadyExistsException.class, () -> userService.create(dup));
        assertThat(ex.getMessage()).isEqualTo("Username taken");
    }

    @Test
    void findByIdShouldReturnUserResponseIfUserExists() {
        UserResponse expectedResponse = userService.create(new CreateUserRequest("test1@example.com", "testuser", "password123"));
        UserResponse receivedResponse = userService.findById(expectedResponse.id());

        assertThat(expectedResponse.email()).isEqualTo(receivedResponse.email());
    }

    @Test
    void findByIdShouldThrowUserNotFoundExceptionIfUserNotExists() {
        UserNotFoundException ex = assertThrows(UserNotFoundException.class, () -> userService.findById(123L));
        assertThat(ex.getMessage()).isEqualTo("User not found with id: " + 123L);
    }
}
