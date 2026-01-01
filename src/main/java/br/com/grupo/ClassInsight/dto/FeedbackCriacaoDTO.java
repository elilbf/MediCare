package br.com.grupo.ClassInsight.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import br.com.grupo.ClassInsight.model.TipoFeedback;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para criação de um novo feedback")
public record FeedbackCriacaoDTO(
    @Schema(description = "ID da turma associada", example = "10")
    @NotNull(message = "ID da turma não pode ser nulo")
    @JsonProperty("turma_id")
    Long turmaId,
    
    @Schema(description = "Título do feedback", example = "Dúvida sobre conteúdo da aula")
    @NotBlank(message = "Título não pode estar vazio")
    @Size(min = 5, max = 150, message = "Título deve ter entre 5 e 150 caracteres")
    String titulo,
    
    @Schema(description = "Conteúdo detalhado do feedback", example = "Não consegui entender o conceito de polimorfismo")
    @NotBlank(message = "Conteúdo não pode estar vazio")
    @Size(min = 10, max = 2000, message = "Conteúdo deve ter entre 10 e 2000 caracteres")
    String conteudo,
    
    @Schema(description = "Tipo do feedback", example = "DUVIDA")
    @NotNull(message = "Tipo de feedback não pode ser nulo")
    @JsonProperty("tipo_feedback")
    TipoFeedback tipoFeedback
) {}
