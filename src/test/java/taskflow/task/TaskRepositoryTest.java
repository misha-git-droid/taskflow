package taskflow.task;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import taskflow.project.Project;
import taskflow.project.ProjectRepository;
import taskflow.user.User;
import taskflow.user.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
public class TaskRepositoryTest {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRepository projectRepository;

    @Test
    void findAllByProjectId_shouldReturnProjectIfExists() {
        User user = userRepository.save(new User("test@test.ru", "test", "password123"));
        Project project = projectRepository.save(new Project("Test Project", "Description", user));
        Long projectId = project.getId();
        taskRepository.save(new Task("Test Task", "Description", TaskStatus.TODO, project, user));

        List<Task> tasks = taskRepository.findAllByProjectId(projectId);
        assertThat(tasks)
                .hasSize(1)
                .extracting(Task::getTitle)
                .containsExactly("Test Task");
    }
}
