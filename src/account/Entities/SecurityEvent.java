package account.Entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "security_events")
public class SecurityEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false)
    private String object;

    @Column(nullable = false)
    private String path;

    public SecurityEvent() {}

    public SecurityEvent(String action, String subject, String object, String path) {
        this.date = LocalDateTime.now();
        this.action = action;
        this.subject = subject;
        this.object = object;
        this.path = path;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getObject() { return object; }
    public void setObject(String object) { this.object = object; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
}