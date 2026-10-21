package com.example.backend.dto;

import jakarta.validation.constraints.*;

public record DentistRequest(
        @NotBlank(message = "Ime je obavezno") @Size(max = 100) String firstName,
        @NotBlank(message = "Prezime je obavezno") @Size(max = 100) String lastName,
        @Size(max = 100) String specialization,
        @Size(max = 30) String phone,
        @Email(message = "Email nije ispravan") String email,
        Boolean active) {}
