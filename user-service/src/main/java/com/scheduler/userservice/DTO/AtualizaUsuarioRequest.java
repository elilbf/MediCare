package com.scheduler.userservice.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class AtualizaUsuarioRequest {
    @Size(min = 1, message = "Nome não pode ser vazio")
    private String nome;

    @Email(message = "Email deve ser válido")
    private String email;

    @Size(min = 8, message = "Senha deve ter pelo menos 8 caracteres")
    private String senha;

    @Size(min = 1, message = "Username não pode ser vazio")
    private String username;

    @Size(min = 1, message = "Deve ter pelo menos uma role")
    private Set<String> roles;
}