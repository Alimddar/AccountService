package account.dtos.Response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class SecurityEventResponse {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime date;
    private String action;
    private String subject;
    private String object;
    private String path;

    public SecurityEventResponse() {}

    public SecurityEventResponse(LocalDateTime date, String action, String subject, String object, String path) {
        this.date = date;
        this.action = action;
        this.subject = subject;
        this.object = object;
        this.path = path;
    }

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