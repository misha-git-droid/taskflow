package taskflow.user;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.assertj.*;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveAndFindUserByEmail() {
        User user = new User("test@example.com", "testuser", "hashedPassword123");

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
        User user = new User("test@example.com", "testuser", "hashedPassword123");
        userRepository.save(user);
        assertThat(user.getCreatedAt()).isNotNull();
        assertThat(user.getUpdatedAt()).isNotNull();
    }
}
