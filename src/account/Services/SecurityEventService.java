package account.Services;

import account.Entities.SecurityEvent;
import account.Repositories.SecurityEventRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SecurityEventService {

    private final SecurityEventRepository securityEventRepository;

    public SecurityEventService(SecurityEventRepository securityEventRepository) {
        this.securityEventRepository = securityEventRepository;
    }

    public void logEvent(String action, String subject, String object, String path) {
        SecurityEvent event = new SecurityEvent(action, subject, object, path);
        securityEventRepository.save(event);
    }

    public List<SecurityEvent> getAllEvents() {
        return securityEventRepository.findAllByOrderByIdAsc();
    }
}