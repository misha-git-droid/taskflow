package taskflow.user;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse create(CreateUserRequest request) {
        String email = request.email();
        String username = request.username();
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username taken");
        }
        User user = new User(email, username, request.password());
        userRepository.save(user);
        return new UserResponse(user.getId(), user.getEmail(), user.getUsername(), user.getCreatedAt());
    }

    public UserResponse findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        Long userId = user.getId();
        String email = user.getEmail();
        String username = user.getUsername();
        Instant createdAt = user.getCreatedAt();
        return new UserResponse(userId, email, username, createdAt);
    }
}
