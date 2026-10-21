package com.example.backend.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record TreatmentRequest(
        @NotBlank(message = "Naziv je obavezan") @Size(max = 150) String name,
        @Size(max = 1000) String description,
        @NotNull(message = "Cijena je obavezna")
        @PositiveOrZero(message = "Cijena ne može biti negativna") BigDecimal price,
        @NotNull(message = "Trajanje je obavezno")
        @Min(value = 5, message = "Trajanje je najmanje 5 minuta") Integer durationMinutes) {}
