package com.scheduler.schedulingservice.dto;

import lombok.Data;
import org.springframework.graphql.data.method.annotation.SchemaMapping;

@Data
@SchemaMapping("UpdateAppointment")
public class UpdateAppointmentDto {
    private Long id;
    private Long patientId;
    private Long doctorId;
    private String appointmentDate;
}