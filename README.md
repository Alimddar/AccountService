# Account Service (Java)

A comprehensive RESTful API for managing employee accounts and payroll information, built with Spring Boot and featuring enterprise-grade security.

## Features

- **Authentication & Authorization**
  - HTTP Basic Authentication with BCrypt password encoding
  - Role-based access control (RBAC) with 4 roles: Administrator, User, Accountant, Auditor
  - Separation of administrative and business user groups

- **Security**
  - SSL/TLS encryption (HTTPS)
  - Brute force attack protection with automatic account locking after 5 failed attempts
  - Breached password detection
  - Comprehensive security event logging (10 event types)

- **User Management**
  - User registration with email validation (@acme.com domain)
  - Password change functionality
  - Role assignment and revocation
  - Account lock/unlock capabilities

- **Payroll Management**
  - Upload and update employee payment records
  - View payment history by period

- **Audit Trail**
  - Security event logging for compliance
  - Events: CREATE_USER, CHANGE_PASSWORD, ACCESS_DENIED, LOGIN_FAILED, GRANT_ROLE, REMOVE_ROLE, LOCK_USER, UNLOCK_USER, DELETE_USER, BRUTE_FORCE

## Tech Stack

- Java 17
- Spring Boot 3.2
- Spring Security
- Spring Data JPA
- H2 Database
- Gradle

## API Endpoints

| Endpoint | Method | Access | Description |
|----------|--------|--------|-------------|
| `/api/auth/signup` | POST | Public | Register new user |
| `/api/auth/changepass` | POST | Authenticated | Change password |
| `/api/empl/payment` | GET | User, Accountant | Get payment info |
| `/api/acct/payments` | POST | Accountant | Upload payrolls |
| `/api/acct/payments` | PUT | Accountant | Update payment |
| `/api/admin/user` | GET | Administrator | List all users |
| `/api/admin/user/{email}` | DELETE | Administrator | Delete user |
| `/api/admin/user/role` | PUT | Administrator | Change user role |
| `/api/admin/user/access` | PUT | Administrator | Lock/unlock user |
| `/api/security/events` | GET | Auditor | View security events |

## Getting Started

1. Clone the repository
2. Place your SSL certificate in `src/main/resources/keystore/service.p12`
3. Run with Gradle: `./gradlew bootRun`
4. Access API at `https://localhost:28852`

## License

MIT
