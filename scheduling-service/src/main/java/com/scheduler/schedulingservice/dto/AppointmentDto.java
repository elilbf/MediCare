package com.scheduler.schedulingservice.dto;

import lombok.Data;
import org.springframework.graphql.data.method.annotation.SchemaMapping;

import java.time.LocalDateTime;

@Data
@SchemaMapping("Appointment")
public class AppointmentDto {
    private Long id;
    private PatientDto patient;
    private DoctorDto doctor;
    private LocalDateTime appointmentDate;
    private LocalDateTime createdDate;
    private LocalDateTime lastUpdateDate;
}
