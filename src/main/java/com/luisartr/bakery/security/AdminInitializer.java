package com.luisartr.bakery.security;

import com.luisartr.bakery.user.User;
import com.luisartr.bakery.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final String adminPassword;

    public AdminInitializer(UserRepository users, PasswordEncoder encoder,
                            @Value("${ADMIN_PASSWORD:admin123}") String adminPassword) {
        this.users = users;
        this.encoder = encoder;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(String... args) {
        if (users.count() == 0) {
            users.save(new User("Administrador", "admin@bakery.local",
                    encoder.encode(adminPassword), "ADMIN"));
        }
    }
}