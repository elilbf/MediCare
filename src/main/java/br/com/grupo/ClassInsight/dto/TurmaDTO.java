package br.com.grupo.ClassInsight.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public record TurmaDTO(
    Long id,
    String nome,
    String descricao,
    @JsonProperty("professor_id")
    Long professorId,
    @JsonProperty("codigo_turma")
    String codigoTurma,
    @JsonProperty("data_criacao")
    LocalDateTime dataCriacao,
    boolean ativa
) {}
