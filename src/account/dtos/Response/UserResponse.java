package account.dtos.Response;

import java.util.List;

public class UserResponse {
    private Long id;
    private String name;
    private String lastname;
    private String email;
    private List<String> roles;

    public UserResponse(Long id, String name, String lastname, String email, List<String> roles) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.roles = roles;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getLastname() { return lastname; }
    public String getEmail() { return email; }
    public List<String> getRoles() { return roles; }
}