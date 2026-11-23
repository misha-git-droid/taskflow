package taskflow.project;

import jakarta.persistence.*;
import taskflow.user.User;

import java.time.Instant;

@Entity
@Table(name = "projects")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
    @Column(name = "created_at")
    private Instant createdAt;
    @Column(name = "updated_at")
    private Instant updatedAt;

    public Project() {}
    public Project(String name, String description, User owner) {
        this.name = name;
        this.description = description;
        this.owner = owner;
    }

    public void setName(String name) {this.name = name;}
    public void setDescription(String description) {this.description = description;}
    public void setOwner(User owner) {this.owner = owner;}
    public String getName() {return this.name;}
    public String getDescription() {return this.description;}
    public User getOwner() {return this.owner;}

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
