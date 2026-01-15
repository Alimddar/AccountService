package account.dtos.Request;

import jakarta.validation.constraints.NotBlank;

public class RoleChangeRequest {

    @NotBlank
    private String user;

    @NotBlank
    private String role;

    @NotBlank
    private String operation;

    public RoleChangeRequest() {}

    public RoleChangeRequest(String user, String role, String operation) {
        this.user = user;
        this.role = role;
        this.operation = operation;
    }

    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }
}