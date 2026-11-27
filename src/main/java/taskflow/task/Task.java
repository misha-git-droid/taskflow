package taskflow.task;

import jakarta.persistence.*;
import taskflow.project.Project;
import taskflow.user.User;

import java.time.Instant;

@Entity
@Table(name = "tasks")
public class Task {
    protected Task() {}
    public Task(String title, String description, TaskStatus status,
                Project project, User assignee) {
            this.title = title;
            this.description = description;
            this.status = status;
            this.project = project;
            this.assignee = assignee;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;
    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
    @ManyToOne
    @JoinColumn(name = "assignee_id")
    private User assignee;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public Long getId() {return this.id;}
    public String getTitle() {return this.title;}
    public String getDescription() {return this.description;}
    public TaskStatus getStatus() {return this.status;}
    public Project getProject() {return this.project;}
    public User getUser() {return this.assignee;}
    public Instant getCreatedAt() {return this.createdAt;}
    public Instant getUpdatedAt() {return this.updatedAt;}

    public void setTitle(String title) {this.title = title;}
    public void setDescription(String description) {this.description = description;}
    public void setStatus(TaskStatus status) {this.status = status;}
    public void setProject(Project project) {this.project = project;}
    public void setAssignee(User assignee) {this.assignee = assignee;}


    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {this.updatedAt = Instant.now();}
}
