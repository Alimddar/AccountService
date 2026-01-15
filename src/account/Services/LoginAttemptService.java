package account.Services;

import account.Entities.User;
import account.Repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private final Map<String, Integer> attemptsCache = new ConcurrentHashMap<>();

    private final UserRepository userRepository;
    private final SecurityEventService securityEventService;

    public LoginAttemptService(UserRepository userRepository, SecurityEventService securityEventService) {
        this.userRepository = userRepository;
        this.securityEventService = securityEventService;
    }

    public void loginSucceeded(String email) {
        attemptsCache.remove(email.toLowerCase());
    }

    public void loginFailed(String email, String path) {
        String lowerEmail = email.toLowerCase();

        securityEventService.logEvent("LOGIN_FAILED", lowerEmail, path, path);

        int attempts = attemptsCache.getOrDefault(lowerEmail, 0) + 1;
        attemptsCache.put(lowerEmail, attempts);

        if (attempts > MAX_ATTEMPTS) {
            userRepository.findByEmail(lowerEmail).ifPresent(user -> {
                boolean isAdmin = user.getRoles().stream()
                        .anyMatch(role -> role.getName().equals("ROLE_ADMINISTRATOR"));

                if (!isAdmin) {
                    securityEventService.logEvent("BRUTE_FORCE", lowerEmail, path, path);

                    user.setAccountNonLocked(false);
                    userRepository.save(user);

                    securityEventService.logEvent("LOCK_USER", lowerEmail, "Lock user " + lowerEmail, path);

                    attemptsCache.remove(lowerEmail);
                }
            });
        }
    }

    public void resetAttempts(String email) {
        attemptsCache.remove(email.toLowerCase());
    }

    public int getAttempts(String email) {
        return attemptsCache.getOrDefault(email.toLowerCase(), 0);
    }
}