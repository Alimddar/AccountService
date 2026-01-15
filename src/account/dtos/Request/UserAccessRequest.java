package account.dtos.Request;

import jakarta.validation.constraints.NotBlank;

public class UserAccessRequest {

    @NotBlank
    private String user;

    @NotBlank
    private String operation;

    public UserAccessRequest() {}

    public UserAccessRequest(String user, String operation) {
        this.user = user;
        this.operation = operation;
    }

    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }

    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }
}