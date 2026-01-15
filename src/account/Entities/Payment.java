package account.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "payments", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"employee", "period"})
})
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Employee must not be empty!")
    private String employee;

    @NotNull(message = "Period must not be empty!")
    @Pattern(regexp = "(0[1-9]|1[0-2])-\\d{4}", message = "Wrong date!")
    private String period;

    @NotNull(message = "Salary must not be empty!")
    @Min(value = 0, message = "Salary must be non negative!")
    private Long salary;

    public Payment() {}

    public Payment(String employee, String period, Long salary) {
        this.employee = employee;
        this.period = period;
        this.salary = salary;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public void setSalary(Long salary) {
        this.salary = salary;
    }

    public Long getId() {
        return id;
    }

    public String getEmployee() {
        return employee;
    }

    public String getPeriod() {
        return period;
    }

    public Long getSalary() {
        return salary;
    }
}