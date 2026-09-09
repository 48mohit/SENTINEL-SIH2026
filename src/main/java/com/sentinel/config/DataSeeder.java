package com.sentinel.config;

import com.sentinel.model.Role;
import com.sentinel.model.User;
import com.sentinel.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            createUser("admin", "Admin@123", "System Admin", "admin@sentinel.gov", Role.ADMIN);
            createUser("officer1", "Officer@123", "Officer Rajesh Kumar", "officer1@sentinel.gov", Role.OFFICER);
            createUser("supervisor1", "Super@123", "Supervisor Priya Singh", "supervisor1@sentinel.gov", Role.SUPERVISOR);
            createUser("auditor1", "Audit@123", "Auditor Amit Sharma", "auditor1@sentinel.gov", Role.AUDITOR);
            System.out.println("Default users created!");
        }
    }

    private void createUser(String username, String password,
            String fullName, String email, Role role) {
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setFullName(fullName);
        user.setEmail(email);
        user.setRole(role);
        user.setActive(true);
        userRepository.save(user);
    }
}