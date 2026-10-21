package com.example.backend.controller;

import com.example.backend.dto.PatientRequest;
import com.example.backend.entity.Patient;
import com.example.backend.exception.ApiException;
import com.example.backend.repository.PatientRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientRepository repository;

    public PatientController(PatientRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Patient> list(@RequestParam(required = false) String q) {
        if (q == null || q.isBlank()) {
            return repository.findAll(Sort.by("lastName", "firstName"));
        }
        return repository.search(q.trim());
    }

    @GetMapping("/{id}")
    public Patient getOne(@PathVariable Long id) {
        return find(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Patient create(@Valid @RequestBody PatientRequest req) {
        return repository.save(apply(new Patient(), req));
    }

    @PutMapping("/{id}")
    public Patient update(@PathVariable Long id, @Valid @RequestBody PatientRequest req) {
        return repository.save(apply(find(id), req));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        repository.delete(find(id));
    }

    private Patient find(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Pacijent nije pronađen"));
    }

    private Patient apply(Patient p, PatientRequest r) {
        p.setFirstName(r.firstName().trim());
        p.setLastName(r.lastName().trim());
        p.setPhone(r.phone().trim());
        p.setEmail(r.email());
        p.setDateOfBirth(r.dateOfBirth());
        p.setNote(r.note());
        return p;
    }
}
