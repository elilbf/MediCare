package com.scheduler.schedulingservice.repositories;

import com.scheduler.schedulingservice.entities.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatientId(Long patientId);

    List<Appointment> findByDoctorId(Long doctorId);

    List<Appointment> findByPatientIdAndDoctorId(Long patientId, Long doctorId);

    @Query("SELECT a FROM Appointment a WHERE a.patientId = :patientId " +
           "AND (:sinceDate IS NULL OR a.appointmentDate >= :sinceDate) " +
           "AND (:untilDate IS NULL OR a.appointmentDate <= :untilDate)")
    List<Appointment> findByPatientIdWithDateRange(@Param("patientId") Long patientId,
                                                   @Param("sinceDate") LocalDateTime sinceDate,
                                                   @Param("untilDate") LocalDateTime untilDate);

    @Query("SELECT a FROM Appointment a WHERE a.doctorId = :doctorId " +
           "AND (:sinceDate IS NULL OR a.appointmentDate >= :sinceDate) " +
           "AND (:untilDate IS NULL OR a.appointmentDate <= :untilDate)")
    List<Appointment> findByDoctorIdWithDateRange(@Param("doctorId") Long doctorId,
                                                  @Param("sinceDate") LocalDateTime sinceDate,
                                                  @Param("untilDate") LocalDateTime untilDate);
}
