package com.example.backend.controller;

import com.example.backend.dto.AppointmentRequest;
import com.example.backend.dto.StatusRequest;
import com.example.backend.entity.Appointment;
import com.example.backend.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService service;

    public AppointmentController(AppointmentService service) {
        this.service = service;
    }

    // GET /api/appointments?date=2026-10-20   (bez datuma = danas)
    @GetMapping
    public List<Appointment> list(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return service.listByDate(date != null ? date : LocalDate.now());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Appointment create(@Valid @RequestBody AppointmentRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public Appointment update(@PathVariable Long id, @Valid @RequestBody AppointmentRequest req) {
        return service.update(id, req);
    }

    @PatchMapping("/{id}/status")
    public Appointment changeStatus(@PathVariable Long id, @Valid @RequestBody StatusRequest req) {
        return service.changeStatus(id, req.status());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
