package account.Controllers;

import account.Entities.SecurityEvent;
import account.Services.SecurityEventService;
import account.dtos.Response.SecurityEventResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/security")
public class SecurityController {

    private final SecurityEventService securityEventService;

    public SecurityController(SecurityEventService securityEventService) {
        this.securityEventService = securityEventService;
    }

    @GetMapping("/events")
    public ResponseEntity<List<SecurityEventResponse>> getSecurityEvents() {
        List<SecurityEventResponse> events = securityEventService.getAllEvents().stream()
                .map(event -> new SecurityEventResponse(
                        event.getDate(),
                        event.getAction(),
                        event.getSubject(),
                        event.getObject(),
                        event.getPath()
                ))
                .collect(Collectors.toList());

        return new ResponseEntity<>(events, HttpStatus.OK);
    }
}