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
    @Query(value = "SELECT * FROM appointments WHERE patient_id = :patientId AND appointment_date::timestamp >= :since AND appointment_date::timestamp <= :until", nativeQuery = true)
    List<Appointment> findByPatientIdAndDateRange(@Param("patientId") Long patientId,
                                                 @Param("since") LocalDateTime since,
                                                 @Param("until") LocalDateTime until);

    // Query for patient appointments with only start date
    @Query(value = "SELECT * FROM appointments WHERE patient_id = :patientId AND appointment_date::timestamp >= :since", nativeQuery = true)
    List<Appointment> findByPatientIdSinceDate(@Param("patientId") Long patientId,
                                              @Param("since") LocalDateTime since);

    // Query for patient appointments with only end date
    @Query(value = "SELECT * FROM appointments WHERE patient_id = :patientId AND appointment_date::timestamp <= :until", nativeQuery = true)
    List<Appointment> findByPatientIdUntilDate(@Param("patientId") Long patientId,
                                              @Param("until") LocalDateTime until);

    // Query for doctor appointments with date range (both dates provided)
    @Query(value = "SELECT * FROM appointments WHERE doctor_id = :doctorId AND appointment_date::timestamp >= :since AND appointment_date::timestamp <= :until", nativeQuery = true)
    List<Appointment> findByDoctorIdAndDateRange(@Param("doctorId") Long doctorId,
                                                @Param("since") LocalDateTime since,
                                                @Param("until") LocalDateTime until);

    // Query for doctor appointments with only start date
    @Query(value = "SELECT * FROM appointments WHERE doctor_id = :doctorId AND appointment_date::timestamp >= :since", nativeQuery = true)
    List<Appointment> findByDoctorIdSinceDate(@Param("doctorId") Long doctorId,
                                             @Param("since") LocalDateTime since);

    // Query for doctor appointments with only end date
    @Query(value = "SELECT * FROM appointments WHERE doctor_id = :doctorId AND appointment_date::timestamp <= :until", nativeQuery = true)
    List<Appointment> findByDoctorIdUntilDate(@Param("doctorId") Long doctorId,
                                             @Param("until") LocalDateTime until);
}
