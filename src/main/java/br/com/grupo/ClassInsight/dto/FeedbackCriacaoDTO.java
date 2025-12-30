package br.com.grupo.ClassInsight.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import br.com.grupo.ClassInsight.model.TipoFeedback;
import com.fasterxml.jackson.annotation.JsonProperty;

public record FeedbackCriacaoDTO(
    @NotNull(message = "ID da turma não pode ser nulo")
    @JsonProperty("turma_id")
    Long turmaId,
    
    @NotBlank(message = "Título não pode estar vazio")
    @Size(min = 5, max = 150, message = "Título deve ter entre 5 e 150 caracteres")
    String titulo,
    
    @NotBlank(message = "Conteúdo não pode estar vazio")
    @Size(min = 10, max = 2000, message = "Conteúdo deve ter entre 10 e 2000 caracteres")
    String conteudo,
    
    @NotNull(message = "Tipo de feedback não pode ser nulo")
    @JsonProperty("tipo_feedback")
    TipoFeedback tipoFeedback
) {}
