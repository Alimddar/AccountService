package account.Services;

import account.Entities.Payment;
import account.Entities.User;
import account.Repositories.PaymentRepository;
import account.Repositories.UserRepository;
import account.dtos.Response.EmployeePaymentResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Validated
public class PaymentService {

    private final PaymentRepository paymentRepo;
    private final UserRepository userRepository;

    public PaymentService(PaymentRepository paymentRepo, UserRepository userRepository) {
        this.paymentRepo = paymentRepo;
        this.userRepository = userRepository;
    }

    @Transactional
    public void savePayments(List<@Valid Payment> payments) {
        Set<String> seen = new HashSet<>();

        for (Payment payment : payments) {
            String key = payment.getEmployee().toLowerCase() + "-" + payment.getPeriod();
            if (!seen.add(key)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate in request body");
            }
            if (paymentRepo.findByEmployeeAndPeriod(payment.getEmployee().toLowerCase(), payment.getPeriod()).isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Period already exists for employee");
            }
            if (!userRepository.existsByEmail(payment.getEmployee().toLowerCase())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Employee not found");
            }

            payment.setEmployee(payment.getEmployee().toLowerCase());
        }
        paymentRepo.saveAll(payments);
    }

    @Transactional
    public void updatePayment(@Valid Payment paymentRequest) {
        if (!userRepository.existsByEmail(paymentRequest.getEmployee().toLowerCase())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Employee not found!");
        }

        Payment existingPayment = paymentRepo
                .findByEmployeeAndPeriod(paymentRequest.getEmployee().toLowerCase(), paymentRequest.getPeriod())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment not found for this period!"));

        existingPayment.setSalary(paymentRequest.getSalary());
        paymentRepo.save(existingPayment);
    }

    public Object getPaymentForEmployee(String email, String period) {
        User user = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        if (period != null) {
            validatePeriod(period);

            Optional<Payment> paymentOpt = paymentRepo.findByEmployeeAndPeriod(email.toLowerCase(), period);

            if (paymentOpt.isEmpty()) {
                return Collections.emptyMap();
            }

            return mapToPaymentDto(user, paymentOpt.get());
        } else {
            List<Payment> payments = paymentRepo.findAllByEmployee(email.toLowerCase());

            if (payments.isEmpty()) {
                return new EmployeePaymentResponse(
                        user.getId(),
                        user.getName(),
                        user.getLastname(),
                        user.getEmail()
                );
            }

            return payments.stream()
                    .sorted((p1, p2) -> {
                        YearMonth date1 = parseYearMonth(p1.getPeriod());
                        YearMonth date2 = parseYearMonth(p2.getPeriod());
                        return date2.compareTo(date1);
                    })
                    .map(p -> mapToPaymentDto(user, p))
                    .collect(Collectors.toList());
        }
    }

    private void validatePeriod(String period) {
        if (!period.matches("(0[1-9]|1[0-2])-\\d{4}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong date format!");
        }
    }

    private YearMonth parseYearMonth(String period) {
        return YearMonth.parse(period, DateTimeFormatter.ofPattern("MM-yyyy"));
    }

    private EmployeePaymentResponse mapToPaymentDto(User user, Payment payment) {
        YearMonth ym = parseYearMonth(payment.getPeriod());
        String monthName = ym.getMonth().toString();
        monthName = monthName.substring(0, 1).toUpperCase() + monthName.substring(1).toLowerCase();
        String formattedDate = monthName + "-" + ym.getYear();

        long dollars = payment.getSalary() / 100;
        long cents = payment.getSalary() % 100;
        String formattedSalary = String.format("%d dollar(s) %d cent(s)", dollars, cents);

        return new EmployeePaymentResponse(
                user.getName(),
                user.getLastname(),
                formattedDate,
                formattedSalary
        );
    }
}