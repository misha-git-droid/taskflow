package taskflow.user;

import org.springframework.stereotype.Service;
import taskflow.exception.EmailAlreadyExistsException;
import taskflow.exception.UserNotFoundException;
import taskflow.exception.UsernameAlreadyExistsException;

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
            throw new EmailAlreadyExistsException("Email already exists");
        }
        if (userRepository.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException("Username taken");
        }
        User user = new User(email, username, request.password());
        userRepository.save(user);
        return new UserResponse(user.getId(), user.getEmail(), user.getUsername(), user.getCreatedAt());
    }

    public UserResponse findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found!"));

        Long userId = user.getId();
        String email = user.getEmail();
        String username = user.getUsername();
        Instant createdAt = user.getCreatedAt();
        return new UserResponse(userId, email, username, createdAt);
    }
}
