package com.example.backend.controller;

import com.example.backend.dto.DentistRequest;
import com.example.backend.entity.Dentist;
import com.example.backend.exception.ApiException;
import com.example.backend.repository.DentistRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dentists")
public class DentistController {

    private final DentistRepository repository;

    public DentistController(DentistRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Dentist> list() {
        return repository.findAll(Sort.by("lastName", "firstName"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Dentist create(@Valid @RequestBody DentistRequest req) {
        return repository.save(apply(new Dentist(), req));
    }

    @PutMapping("/{id}")
    public Dentist update(@PathVariable Long id, @Valid @RequestBody DentistRequest req) {
        return repository.save(apply(find(id), req));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        repository.delete(find(id));
    }

    private Dentist find(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Zubar nije pronađen"));
    }

    private Dentist apply(Dentist d, DentistRequest r) {
        d.setFirstName(r.firstName().trim());
        d.setLastName(r.lastName().trim());
        d.setSpecialization(r.specialization());
        d.setPhone(r.phone());
        d.setEmail(r.email());
        d.setActive(r.active() == null || r.active());
        return d;
    }
}
