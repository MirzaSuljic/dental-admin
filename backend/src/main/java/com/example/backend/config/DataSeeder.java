package com.example.backend.config;

import com.example.backend.entity.AdminUser;
import com.example.backend.repository.AdminUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final AdminUserRepository admins;
    private final BCryptPasswordEncoder encoder;

    @Value("${app.admin.email}")
    private String email;

    @Value("${app.admin.password}")
    private String password;

    public DataSeeder(AdminUserRepository admins, BCryptPasswordEncoder encoder) {
        this.admins = admins;
        this.encoder = encoder;
    }

    // izvrši se jednom pri svakom pokretanju aplikacije
    @Override
    public void run(String... args) {
        if (admins.count() == 0) {
            AdminUser admin = new AdminUser();
            admin.setEmail(email.trim().toLowerCase());
            admin.setPasswordHash(encoder.encode(password));
            admins.save(admin);
            System.out.println("Napravljen prvi administrator: " + email);
        }
    }
}
