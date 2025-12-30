package br.com.grupo.ClassInsight.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import br.com.grupo.ClassInsight.model.TipoUsuario;
import java.time.LocalDateTime;

public record UsuarioDTO(
    Long id,
    String email,
    String nome,
    @JsonProperty("tipo_usuario")
    TipoUsuario tipoUsuario,
    @JsonProperty("data_criacao")
    LocalDateTime dataCriacao,
    boolean ativo
) {}
