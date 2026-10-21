package com.example.backend.controller;

import com.example.backend.dto.TreatmentRequest;
import com.example.backend.entity.Treatment;
import com.example.backend.exception.ApiException;
import com.example.backend.repository.TreatmentRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/treatments")
public class TreatmentController {

    private final TreatmentRepository repository;

    public TreatmentController(TreatmentRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Treatment> list() {
        return repository.findAll(Sort.by("name"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Treatment create(@Valid @RequestBody TreatmentRequest req) {
        return repository.save(apply(new Treatment(), req));
    }

    @PutMapping("/{id}")
    public Treatment update(@PathVariable Long id, @Valid @RequestBody TreatmentRequest req) {
        return repository.save(apply(find(id), req));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        repository.delete(find(id));
    }

    private Treatment find(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Usluga nije pronađena"));
    }

    private Treatment apply(Treatment t, TreatmentRequest r) {
        t.setName(r.name().trim());
        t.setDescription(r.description());
        t.setPrice(r.price());
        t.setDurationMinutes(r.durationMinutes());
        return t;
    }
}
