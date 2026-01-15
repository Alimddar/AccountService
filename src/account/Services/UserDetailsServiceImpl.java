package account.Services;

import account.Entities.Role;
import account.Entities.User;
import account.Exceptions.UserExistException;
import account.Repositories.RoleRepository;
import account.Repositories.UserRepository;
import account.dtos.Response.ChangePasswordResponse;
import account.dtos.Request.SignupRequest;
import account.dtos.Response.UserResponse;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final List<String> breachedPasswords = List.of(
            "PasswordForJanuary", "PasswordForFebruary", "PasswordForMarch", "PasswordForApril",
            "PasswordForMay", "PasswordForJune", "PasswordForJuly", "PasswordForAugust",
            "PasswordForSeptember", "PasswordForOctober", "PasswordForNovember", "PasswordForDecember"
    );

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityEventService securityEventService;

    public UserDetailsServiceImpl(UserRepository userRepository, 
                                   RoleRepository roleRepository,
                                   PasswordEncoder passwordEncoder,
                                   SecurityEventService securityEventService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.securityEventService = securityEventService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Collection<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(authorities)
                .accountLocked(!user.isAccountNonLocked())
                .build();
    }

    public ChangePasswordResponse changePassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        validatePasswordSafety(newPassword);

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The passwords must be different!");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        securityEventService.logEvent("CHANGE_PASSWORD", email.toLowerCase(), email.toLowerCase(), "/api/auth/changepass");

        return new ChangePasswordResponse(
                user.getEmail(),
                "The password has been updated successfully"
        );
    }

    private void validatePasswordSafety(String password) {
        if (breachedPasswords.contains(password)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The password is in the hacker's database!");
        }
    }

    public UserResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail().toLowerCase())) {
            throw new UserExistException("User exist!");
        }

        validatePasswordSafety(request.getPassword());

        User user = new User();
        user.setName(request.getName());
        user.setLastname(request.getLastname());
        user.setEmail(request.getEmail().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setAccountNonLocked(true);

        boolean isFirstUser = userRepository.count() == 0;
        String roleName = isFirstUser ? "ROLE_ADMINISTRATOR" : "ROLE_USER";
        
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Role not found"));
        
        user.addRole(role);

        userRepository.save(user);

        securityEventService.logEvent("CREATE_USER", "Anonymous", user.getEmail(), "/api/auth/signup");

        List<String> roles = user.getRoles().stream()
                .map(r -> r.getName().replace("ROLE_", ""))
                .sorted()
                .collect(Collectors.toList());

        return new UserResponse(user.getId(), user.getName(), user.getLastname(), user.getEmail(), roles);
    }
}