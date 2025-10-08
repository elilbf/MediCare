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

    // Query for patient appointments with date range (both dates provided)
    @Query("""
        SELECT a FROM Appointment a
        WHERE a.patientId = :patientId
          AND a.appointmentDate >= :since
          AND a.appointmentDate <= :until
    """)
    List<Appointment> findByPatientIdAndDateRange(@Param("patientId") Long patientId,
                                                 @Param("since") LocalDateTime since,
                                                 @Param("until") LocalDateTime until);

    // Query for patient appointments with only start date
    @Query("""
        SELECT a FROM Appointment a
        WHERE a.patientId = :patientId
          AND a.appointmentDate >= :since
    """)
    List<Appointment> findByPatientIdSinceDate(@Param("patientId") Long patientId,
                                              @Param("since") LocalDateTime since);

    // Query for patient appointments with only end date
    @Query("""
        SELECT a FROM Appointment a
        WHERE a.patientId = :patientId
          AND a.appointmentDate <= :until
    """)
    List<Appointment> findByPatientIdUntilDate(@Param("patientId") Long patientId,
                                              @Param("until") LocalDateTime until);

    // Query for doctor appointments with date range (both dates provided)
    @Query("""
        SELECT a FROM Appointment a
        WHERE a.doctorId = :doctorId
          AND a.appointmentDate >= :since
          AND a.appointmentDate <= :until
    """)
    List<Appointment> findByDoctorIdAndDateRange(@Param("doctorId") Long doctorId,
                                                @Param("since") LocalDateTime since,
                                                @Param("until") LocalDateTime until);

    // Query for doctor appointments with only start date
    @Query("""
        SELECT a FROM Appointment a
        WHERE a.doctorId = :doctorId
          AND a.appointmentDate >= :since
    """)
    List<Appointment> findByDoctorIdSinceDate(@Param("doctorId") Long doctorId,
                                             @Param("since") LocalDateTime since);

    // Query for doctor appointments with only end date
    @Query("""
        SELECT a FROM Appointment a
        WHERE a.doctorId = :doctorId
          AND a.appointmentDate <= :until
    """)
    List<Appointment> findByDoctorIdUntilDate(@Param("doctorId") Long doctorId,
                                             @Param("until") LocalDateTime until);
}
