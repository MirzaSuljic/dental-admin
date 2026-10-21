package com.example.backend.dto;

import com.example.backend.entity.AppointmentStatus;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public record AppointmentRequest(
        @NotNull(message = "Izaberite pacijenta") Long patientId,
        @NotNull(message = "Izaberite zubara") Long dentistId,
        @NotNull(message = "Izaberite uslugu") Long treatmentId,
        @NotNull(message = "Unesite vrijeme termina") LocalDateTime startTime,
        AppointmentStatus status,
        @Size(max = 1000) String note) {}
