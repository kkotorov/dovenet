package com.richmax.dovenet.config;

import com.richmax.dovenet.repository.UserRepository;
import com.richmax.dovenet.repository.data.User;
import com.richmax.dovenet.types.SubscriptionType;
import com.richmax.dovenet.types.UserRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByUsername(adminUsername).isEmpty()) {
            User admin = new User();
            admin.setUsername(adminUsername);
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole(UserRole.ADMIN);
            admin.setSubscription(SubscriptionType.PRO); // Admin gets everything
            admin.setEmailVerified(true);
            admin.setFirstName("Super");
            admin.setLastName("Admin");
            
            userRepository.save(admin);
            System.out.println("ADMIN ACCOUNT CREATED: username=" + adminUsername);
        }
    }
}
