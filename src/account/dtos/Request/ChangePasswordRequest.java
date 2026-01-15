package account.dtos.Request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

public class ChangePasswordRequest {

    @NotBlank
    @JsonProperty("new_password")
    @Size(min = 12, message = "Password length must be 12 chars minimum!")
    private String newPassword;

    public ChangePasswordRequest() {}
    public ChangePasswordRequest(String newPassword) { this.newPassword = newPassword; }

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}