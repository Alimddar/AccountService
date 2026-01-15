package account.Configs;

import account.Entities.Role;
import account.Repositories.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public DataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {
        createRoleIfNotExists("ROLE_ADMINISTRATOR");
        createRoleIfNotExists("ROLE_USER");
        createRoleIfNotExists("ROLE_ACCOUNTANT");
        createRoleIfNotExists("ROLE_AUDITOR");
    }

    private void createRoleIfNotExists(String roleName) {
        if (!roleRepository.existsByName(roleName)) {
            roleRepository.save(new Role(roleName));
        }
    }
}