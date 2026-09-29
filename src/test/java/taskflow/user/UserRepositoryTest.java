package taskflow.user;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import taskflow.exception.EmailAlreadyExistsException;
import taskflow.exception.UserNotFoundException;
import taskflow.exception.UsernameAlreadyExistsException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private UserController userController;

    @Test
    void shouldSaveAndFindUserByEmail() {
        User user = new User("test@example.com", "testuser", "password123");

        User saved = userRepository.save(user);
        Optional<User> found = userRepository.findByEmail("test@example.com");

        assertThat(saved.getId()).isNotNull();
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    void shouldReturnEmptyWhenUserNotFound() {
        assertThat(userRepository.findByEmail("nonexistent@example.com")).isEmpty();
    }

    @Test
    void shouldDetectExistingEmail() {
       userRepository.save(new User("dup@example.com", "user1", "hash"));
       assertThat(userRepository.existsByEmail("dup@example.com")).isTrue();

    }

    @Test
    void createAtAndUpdatedAtNotNull() {
        User user = new User("test@example.com", "testuser", "password123");
        userRepository.save(user);
        assertThat(user.getCreatedAt()).isNotNull();
        assertThat(user.getUpdatedAt()).isNotNull();
    }

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
        assertThat(ex.getMessage()).isEqualTo("User not found!");
    }
}
