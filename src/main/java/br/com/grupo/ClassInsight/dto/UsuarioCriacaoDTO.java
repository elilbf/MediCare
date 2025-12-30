package br.com.grupo.ClassInsight.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import br.com.grupo.ClassInsight.model.TipoUsuario;
import com.fasterxml.jackson.annotation.JsonProperty;

public record UsuarioCriacaoDTO(
    @NotBlank(message = "Email não pode estar vazio")
    @Email(message = "Email deve ser válido")
    String email,
    
    @NotBlank(message = "Nome não pode estar vazio")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    String nome,
    
    @NotBlank(message = "Senha não pode estar vazia")
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    String senha,
    
    @JsonProperty("tipo_usuario")
    TipoUsuario tipoUsuario
) {}
