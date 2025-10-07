package com.scheduler.schedulingservice.dto;

import lombok.Data;
import java.util.Set;

@Data
public class UsuarioDto {
    private Long id;
    private String email;
    private String nome;
    private String username;
    private Set<String> roles;
}