package com.swapnanil.jobtracker.config;

import com.swapnanil.jobtracker.entity.Role;
import com.swapnanil.jobtracker.entity.User;
import com.swapnanil.jobtracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/** Creates a default admin account on first start (change the credentials via environment variables). */
@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder,
                                @Value("${app.admin.email}") String adminEmail,
                                @Value("${app.admin.password}") String adminPassword) {
        return args -> {
            String email = adminEmail.toLowerCase();
            if (!userRepository.existsByEmail(email)) {
                userRepository.save(new User("Admin", email, passwordEncoder.encode(adminPassword), Role.ADMIN));
            }
        };
    }
}
