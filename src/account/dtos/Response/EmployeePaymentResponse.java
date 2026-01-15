package account.dtos.Response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeePaymentResponse {
    private Long id;
    private String name;
    private String lastname;
    private String email;
    private String period;
    private String salary;

    public EmployeePaymentResponse(Long id, String name, String lastname, String email) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
    }

    public EmployeePaymentResponse(String name, String lastname, String period, String salary) {
        this.name = name;
        this.lastname = lastname;
        this.period = period;
        this.salary = salary;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLastname() { return lastname; }
    public void setLastname(String lastname) { this.lastname = lastname; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }

    public String getSalary() { return salary; }
    public void setSalary(String salary) { this.salary = salary; }
}