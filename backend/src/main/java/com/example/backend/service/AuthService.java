package com.example.backend.service;

import com.example.backend.dto.LoginRequest;
import com.example.backend.dto.LoginResponse;
import com.example.backend.entity.AdminUser;
import com.example.backend.exception.ApiException;
import com.example.backend.repository.AdminUserRepository;
import com.example.backend.security.JwtService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AdminUserRepository admins;
    private final BCryptPasswordEncoder encoder;
    private final JwtService jwt;

    public AuthService(AdminUserRepository admins, BCryptPasswordEncoder encoder,
                       JwtService jwt) {
        this.admins = admins;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    public LoginResponse login(LoginRequest in) {
        AdminUser admin = admins.findByEmail(in.email().trim().toLowerCase()).orElse(null);
        if (admin == null || !encoder.matches(in.password(), admin.getPasswordHash())) {
            throw ApiException.unauthorized("Pogrešan email ili lozinka");
        }
        return new LoginResponse(jwt.sign(admin.getId().toString()), admin.getEmail());
    }
}
