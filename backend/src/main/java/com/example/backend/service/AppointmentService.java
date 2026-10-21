package com.example.backend.service;

import com.example.backend.dto.AppointmentRequest;
import com.example.backend.entity.*;
import com.example.backend.exception.ApiException;
import com.example.backend.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointments;
    private final PatientRepository patients;
    private final DentistRepository dentists;
    private final TreatmentRepository treatments;

    public AppointmentService(AppointmentRepository appointments, PatientRepository patients,
                              DentistRepository dentists, TreatmentRepository treatments) {
        this.appointments = appointments;
        this.patients = patients;
        this.dentists = dentists;
        this.treatments = treatments;
    }

    public List<Appointment> listByDate(LocalDate date) {
        return appointments.findInRange(date.atStartOfDay(), date.plusDays(1).atStartOfDay());
    }

    public Appointment create(AppointmentRequest req) {
        return save(new Appointment(), req, -1L);   // -1 = ne isključuj nijedan termin
    }

    public Appointment update(Long id, AppointmentRequest req) {
        return save(find(id), req, id);
    }

    public Appointment changeStatus(Long id, AppointmentStatus status) {
        Appointment a = find(id);
        checkFree(a.getDentist().getId(), a.getStartTime(), a.getEndTime(), status, id);
        a.setStatus(status);
        return appointments.save(a);
    }

    public void delete(Long id) {
        appointments.delete(find(id));
    }

    private Appointment save(Appointment a, AppointmentRequest req, Long excludeId) {
        Patient patient = patients.findById(req.patientId())
                .orElseThrow(() -> ApiException.badRequest("Pacijent ne postoji"));
        Dentist dentist = dentists.findById(req.dentistId())
                .orElseThrow(() -> ApiException.badRequest("Zubar ne postoji"));
        Treatment treatment = treatments.findById(req.treatmentId())
                .orElseThrow(() -> ApiException.badRequest("Usluga ne postoji"));

        LocalDateTime start = req.startTime();
        LocalDateTime end = start.plusMinutes(treatment.getDurationMinutes());
        AppointmentStatus status = req.status() != null ? req.status() : a.getStatus();

        checkFree(dentist.getId(), start, end, status, excludeId);

        a.setPatient(patient);
        a.setDentist(dentist);
        a.setTreatment(treatment);
        a.setStartTime(start);
        a.setEndTime(end);
        a.setStatus(status);
        a.setNote(req.note());
        return appointments.save(a);
    }

    private void checkFree(Long dentistId, LocalDateTime start, LocalDateTime end,
                           AppointmentStatus status, Long excludeId) {
        if (status != AppointmentStatus.CANCELLED
                && appointments.countOverlapping(dentistId, AppointmentStatus.CANCELLED,
                start, end, excludeId) > 0) {
            throw ApiException.conflict("Zubar već ima termin u tom vremenu");
        }
    }

    private Appointment find(Long id) {
        return appointments.findById(id)
                .orElseThrow(() -> ApiException.notFound("Termin nije pronađen"));
    }
}
