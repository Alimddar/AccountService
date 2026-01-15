package account.Controllers;

import account.Services.AdminService;
import account.dtos.Request.RoleChangeRequest;
import account.dtos.Request.UserAccessRequest;
import account.dtos.Response.DeleteUserResponse;
import account.dtos.Response.StatusResponse;
import account.dtos.Response.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PutMapping("/user/role")
    public ResponseEntity<UserResponse> changeRole(@Valid @RequestBody RoleChangeRequest request,
                                                   @AuthenticationPrincipal UserDetails userDetails) {
        UserResponse response = adminService.changeRole(request, userDetails.getUsername());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/user/{email}")
    public ResponseEntity<DeleteUserResponse> deleteUser(@PathVariable String email,
                                                         @AuthenticationPrincipal UserDetails userDetails) {
        DeleteUserResponse response = adminService.deleteUser(email, userDetails.getUsername());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = adminService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PutMapping("/user/access")
    public ResponseEntity<StatusResponse> changeUserAccess(@Valid @RequestBody UserAccessRequest request,
                                                           @AuthenticationPrincipal UserDetails userDetails) {
        StatusResponse response = adminService.changeUserAccess(request, userDetails.getUsername());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}