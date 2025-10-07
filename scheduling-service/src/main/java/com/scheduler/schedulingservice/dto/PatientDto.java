package com.scheduler.schedulingservice.dto;

import lombok.Data;
import org.springframework.graphql.data.method.annotation.SchemaMapping;

@Data
@SchemaMapping("Patient")
public class PatientDto {
    private Long id;
    private String name;
    private String email;
}
