package taskflow.project;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import taskflow.user.User;
import taskflow.user.UserRepository;


import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
public class ProjectRepositoryTest {
    private static final Logger log = LoggerFactory.getLogger(ProjectRepositoryTest.class);
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void findAllByOwner_shouldReturnAllUserProjects() {
        String passwordEncode = passwordEncoder.encode("password123");
        User user = userRepository.save(new User("test@test.ru", "test", passwordEncode));
        Project project = projectRepository.save(new Project("Project", "Description", user));
        Long userId = user.getId();

        assertThat(projectRepository.findAllByOwnerId(userId).stream().anyMatch(projectName -> projectName.getName().equals(project.getName())));
    }
}
