package com.example.backend.dto;

import com.example.backend.entity.AppointmentStatus;
import jakarta.validation.constraints.NotNull;

public record StatusRequest(@NotNull AppointmentStatus status) {}
