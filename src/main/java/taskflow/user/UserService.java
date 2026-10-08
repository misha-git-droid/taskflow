package taskflow.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import taskflow.infrastructure.exception.EmailAlreadyExistsException;
import taskflow.infrastructure.exception.UserNotFoundException;
import taskflow.infrastructure.exception.UsernameAlreadyExistsException;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
    PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse create(CreateUserRequest request) {
        String email = request.email();
        String username = request.username();
        String passwordHash = passwordEncoder.encode(request.password());
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException("Email already exists");
        }
        if (userRepository.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException("Username taken");
        }
        User user = new User(email, username, passwordHash);
        userRepository.save(user);
        return new UserResponse(user.getId(), user.getEmail(), user.getUsername(), user.getCreatedAt());
    }

    public UserResponse findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return new UserResponse(user.getId(), user.getEmail(), user.getUsername(), user.getCreatedAt());
    }
}
