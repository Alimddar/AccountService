package account.Services;

import account.Entities.Role;
import account.Entities.User;
import account.Repositories.RoleRepository;
import account.Repositories.UserRepository;
import account.dtos.Request.RoleChangeRequest;
import account.dtos.Request.UserAccessRequest;
import account.dtos.Response.DeleteUserResponse;
import account.dtos.Response.StatusResponse;
import account.dtos.Response.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private static final Set<String> ADMINISTRATIVE_ROLES = Set.of("ROLE_ADMINISTRATOR");
    private static final Set<String> BUSINESS_ROLES = Set.of("ROLE_USER", "ROLE_ACCOUNTANT", "ROLE_AUDITOR");
    private static final Set<String> VALID_ROLES = Set.of("ADMINISTRATOR", "USER", "ACCOUNTANT", "AUDITOR");

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SecurityEventService securityEventService;
    private final LoginAttemptService loginAttemptService;

    public AdminService(UserRepository userRepository,
                        RoleRepository roleRepository,
                        SecurityEventService securityEventService,
                        LoginAttemptService loginAttemptService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.securityEventService = securityEventService;
        this.loginAttemptService = loginAttemptService;
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .sorted(Comparator.comparing(User::getId))
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    public DeleteUserResponse deleteUser(String email, String adminEmail) {
        User user = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!"));

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ROLE_ADMINISTRATOR"));

        if (isAdmin) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");
        }

        userRepository.delete(user);

        securityEventService.logEvent("DELETE_USER", adminEmail, email.toLowerCase(), "/api/admin/user");

        return new DeleteUserResponse(email.toLowerCase(), "Deleted successfully!");
    }

    public UserResponse changeRole(RoleChangeRequest request, String adminEmail) {
        User user = userRepository.findByEmail(request.getUser().toLowerCase())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!"));

        String roleName = request.getRole().toUpperCase();

        if (!VALID_ROLES.contains(roleName)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found!");
        }

        String fullRoleName = "ROLE_" + roleName;
        Role role = roleRepository.findByName(fullRoleName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found!"));

        String operation = request.getOperation().toUpperCase();

        if ("GRANT".equals(operation)) {
            grantRole(user, role, fullRoleName);
            securityEventService.logEvent("GRANT_ROLE", adminEmail,
                    "Grant role " + roleName + " to " + user.getEmail(), "/api/admin/user/role");
        } else if ("REMOVE".equals(operation)) {
            removeRole(user, role, fullRoleName);
            securityEventService.logEvent("REMOVE_ROLE", adminEmail,
                    "Remove role " + roleName + " from " + user.getEmail(), "/api/admin/user/role");
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid operation!");
        }

        userRepository.save(user);
        return mapToUserResponse(user);
    }

    public StatusResponse changeUserAccess(UserAccessRequest request, String adminEmail) {
        User user = userRepository.findByEmail(request.getUser().toLowerCase())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!"));

        String operation = request.getOperation().toUpperCase();

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ROLE_ADMINISTRATOR"));

        if ("LOCK".equals(operation)) {
            if (isAdmin) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't lock the ADMINISTRATOR!");
            }
            user.setAccountNonLocked(false);
            userRepository.save(user);

            securityEventService.logEvent("LOCK_USER", adminEmail,
                    "Lock user " + user.getEmail(), "/api/admin/user/access");

            return new StatusResponse("User " + user.getEmail() + " locked!");
        } else if ("UNLOCK".equals(operation)) {
            user.setAccountNonLocked(true);
            userRepository.save(user);

            loginAttemptService.resetAttempts(user.getEmail());

            securityEventService.logEvent("UNLOCK_USER", adminEmail,
                    "Unlock user " + user.getEmail(), "/api/admin/user/access");

            return new StatusResponse("User " + user.getEmail() + " unlocked!");
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid operation!");
        }
    }

    private void grantRole(User user, Role role, String fullRoleName) {
        Set<String> currentRoleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        boolean hasAdministrative = currentRoleNames.stream().anyMatch(ADMINISTRATIVE_ROLES::contains);
        boolean hasBusiness = currentRoleNames.stream().anyMatch(BUSINESS_ROLES::contains);

        boolean isGrantingAdministrative = ADMINISTRATIVE_ROLES.contains(fullRoleName);
        boolean isGrantingBusiness = BUSINESS_ROLES.contains(fullRoleName);

        if ((hasAdministrative && isGrantingBusiness) || (hasBusiness && isGrantingAdministrative)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The user cannot combine administrative and business roles!");
        }

        user.addRole(role);
    }

    private void removeRole(User user, Role role, String fullRoleName) {
        if ("ROLE_ADMINISTRATOR".equals(fullRoleName)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");
        }

        boolean hasRole = user.getRoles().stream()
                .anyMatch(r -> r.getName().equals(fullRoleName));

        if (!hasRole) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user does not have a role!");
        }

        if (user.getRoles().size() == 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user must have at least one role!");
        }

        user.removeRole(role);
    }

    private UserResponse mapToUserResponse(User user) {
        List<String> roles = user.getRoles().stream()
                .map(role -> role.getName().replace("ROLE_", ""))
                .sorted()
                .collect(Collectors.toList());

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getLastname(),
                user.getEmail(),
                roles
        );
    }
}