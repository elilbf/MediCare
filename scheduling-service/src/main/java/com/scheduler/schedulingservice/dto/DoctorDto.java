package com.scheduler.schedulingservice.dto;

import lombok.Data;
import org.springframework.graphql.data.method.annotation.SchemaMapping;

@Data
@SchemaMapping("Doctor")
public class DoctorDto {
    private Long id;
    private String name;
    private String email;
}
