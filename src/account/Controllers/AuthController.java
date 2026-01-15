package account.Controllers;

import account.Services.UserDetailsServiceImpl;
import account.dtos.Request.ChangePasswordRequest;
import account.dtos.Response.ChangePasswordResponse;
import account.dtos.Response.UserResponse;
import account.dtos.Request.SignupRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserDetailsServiceImpl userService;

    public AuthController(UserDetailsServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public UserResponse signup(@Valid @RequestBody SignupRequest request) {
        return userService.signup(request);
    }

    @PostMapping("/changepass")
    public ChangePasswordResponse changepassword(@AuthenticationPrincipal UserDetails userDetails,
                                                 @Valid @RequestBody ChangePasswordRequest request) {
        String email = userDetails.getUsername();
        return userService.changePassword(email, request.getNewPassword());
    }
}