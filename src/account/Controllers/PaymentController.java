package account.Controllers;

import account.Entities.Payment;
import account.Services.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Validated
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/empl/payment")
    public ResponseEntity<?> getPayment(@AuthenticationPrincipal UserDetails userDetails,
                                        @RequestParam(required = false) String period) {
        Object response = paymentService.getPaymentForEmployee(userDetails.getUsername(), period);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/acct/payments")
    public ResponseEntity<?> uploadPayrolls(@RequestBody List<@Valid Payment> payments) {
        paymentService.savePayments(payments);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Map.of("status", "Added successfully!"));
    }

    @PutMapping("/acct/payments")
    public ResponseEntity<?> updatePayment(@RequestBody @Valid Payment payment) {
        paymentService.updatePayment(payment);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Map.of("status", "Updated successfully!"));
    }
}