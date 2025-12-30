package br.com.grupo.ClassInsight.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonProperty;

public record TurmaCriacaoDTO(
    @NotBlank(message = "Nome não pode estar vazio")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    String nome,
    
    @NotBlank(message = "Descrição não pode estar vazia")
    @Size(min = 10, max = 500, message = "Descrição deve ter entre 10 e 500 caracteres")
    String descricao,
    
    @NotNull(message = "ID do professor não pode ser nulo")
    @JsonProperty("professor_id")
    Long professorId
) {}
