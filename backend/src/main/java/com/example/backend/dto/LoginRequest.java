package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Unesite email") String email,
        @NotBlank(message = "Unesite lozinku") String password) {}
