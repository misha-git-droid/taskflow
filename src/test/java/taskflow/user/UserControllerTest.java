package taskflow.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import taskflow.auth.AuthenticationService;
import taskflow.auth.LoginRequest;
import taskflow.auth.LoginResponse;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void create_returnStatus200IfUserCreated() throws Exception {
        CreateUserRequest userRequest = new CreateUserRequest("test@test.ru", "test", "password123");
        mockMvc.perform(post("/api/v1/users").contentType(MediaType.APPLICATION_JSON).content("{\"email\":\"test@test.ru\",\"username\":\"test\",\"password\":\"password123\"}")).andExpect(status().isCreated());
    }

    @Test
    void findById_shouldReturnIdIfMatchesWithOwn() throws Exception {
        UserResponse response = userService.create(new CreateUserRequest("test@test.ru", "test", "password123"));
        Long userId = response.id();
        LoginResponse loginResponse = authenticationService.login(new LoginRequest("test@test.ru", "password123"));

        mockMvc.perform(get("/api/v1/users/{id}", userId).header("Authorization", "Bearer " + loginResponse.token())).andExpect(status().isOk());
    }

    @Test
    void findById_returnStatus403IfFindAlienId() throws Exception {
        UserResponse response = userService.create(new CreateUserRequest("test@test.ru", "test", "password123"));
        Long alienId = 9999L;
        LoginResponse loginResponse = authenticationService.login(new LoginRequest("test@test.ru", "password123"));

        mockMvc.perform(get("/api/v1/users/{id}", alienId).header("Authorization", "Bearer " + loginResponse.token())).andExpect(status().isForbidden());
    }

    @Test
    void findById_returnStatus200IfRoleAdminAndIdAlien() throws Exception {
        String encodePassword = passwordEncoder.encode("password123");
        userRepository.save(new User("test@test.ru", "test", encodePassword, Role.ADMIN));
        String encodePasswordOtherUser = passwordEncoder.encode("password123");
        User otherUser = userRepository.save(new User("testOtherUser@test.ru", "otherUser", encodePasswordOtherUser));
        Long idOtherUser = otherUser.getId();
        LoginResponse loginResponse = authenticationService.login(new LoginRequest("test@test.ru", "password123"));

        mockMvc.perform(get("/api/v1/users/{id}", idOtherUser).header("Authorization", "Bearer " + loginResponse.token())).andExpect(status().isOk());
    }

    @Test
    void delete_returnStatus204IfRoleAdmin() throws Exception {
        String encodePassword = passwordEncoder.encode("password123");
        userRepository.save(new User("test@test.ru", "test", encodePassword, Role.ADMIN));
        String encodePasswordOtherUser = passwordEncoder.encode("password123");
        User otherUser = userRepository.save(new User("testOtherUser@test.ru", "otherUser", encodePasswordOtherUser));
        Long idOtherUser = otherUser.getId();
        LoginResponse loginResponse = authenticationService.login(new LoginRequest("test@test.ru", "password123"));

        mockMvc.perform(delete("/api/v1/users/{id}", idOtherUser).header("Authorization", "Bearer " + loginResponse.token())).andExpect(status().isNoContent());
    }

    @Test
    void delete_returnStatus403IfRoleUser() throws Exception {
        String encodePassword = passwordEncoder.encode("password123");
        userRepository.save(new User("test@test.ru", "test", encodePassword));
        String encodePasswordOtherUser = passwordEncoder.encode("password123");
        User otherUser = userRepository.save(new User("testOtherUser@test.ru", "otherUser", encodePasswordOtherUser));
        Long idOtherUser = otherUser.getId();

        mockMvc.perform(delete("/api/v1/users/{id}", idOtherUser)).andExpect(status().isForbidden());

    }
}
