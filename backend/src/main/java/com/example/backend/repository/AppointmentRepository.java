package com.example.backend.repository;

import com.example.backend.entity.Appointment;
import com.example.backend.entity.AppointmentStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // termini u jednom danu, zajedno sa pacijentom, zubarom i uslugom
    @EntityGraph(attributePaths = {"patient", "dentist", "treatment"})
    @Query("""
        select a from Appointment a
        where a.startTime >= :from and a.startTime < :to
        order by a.startTime
        """)
    List<Appointment> findInRange(@Param("from") LocalDateTime from,
                                  @Param("to") LocalDateTime to);

    // koliko termina istog zubara se preklapa sa [start, end)
    @Query("""
        select count(a) from Appointment a
        where a.dentist.id = :dentistId
          and a.status <> :cancelled
          and a.startTime < :end
          and a.endTime > :start
          and a.id <> :excludeId
        """)
    long countOverlapping(@Param("dentistId") Long dentistId,
                          @Param("cancelled") AppointmentStatus cancelled,
                          @Param("start") LocalDateTime start,
                          @Param("end") LocalDateTime end,
                          @Param("excludeId") Long excludeId);
}
