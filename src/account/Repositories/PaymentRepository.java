package account.Repositories;

import account.Entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByEmployeeAndPeriod(String employee, String period);
    List<Payment> findAllByEmployee(String employee);
}