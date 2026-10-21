package com.example.backend.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record PatientRequest(
        @NotBlank(message = "Ime je obavezno") @Size(max = 100) String firstName,
        @NotBlank(message = "Prezime je obavezno") @Size(max = 100) String lastName,
        @NotBlank(message = "Telefon je obavezan") @Size(max = 30) String phone,
        @Email(message = "Email nije ispravan") String email,
        LocalDate dateOfBirth,
        @Size(max = 2000) String note) {}
