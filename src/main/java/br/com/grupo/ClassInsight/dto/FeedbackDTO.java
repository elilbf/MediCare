package br.com.grupo.ClassInsight.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import br.com.grupo.ClassInsight.model.TipoFeedback;
import br.com.grupo.ClassInsight.model.StatusFeedback;
import java.time.LocalDateTime;

public record FeedbackDTO(
    Long id,
    @JsonProperty("turma_id")
    Long turmaId,
    @JsonProperty("aluno_id")
    Long alunoId,
    String titulo,
    String conteudo,
    @JsonProperty("tipo_feedback")
    TipoFeedback tipoFeedback,
    StatusFeedback status,
    @JsonProperty("data_criacao")
    LocalDateTime dataCriacao,
    String resposta,
    @JsonProperty("data_resposta")
    LocalDateTime dataResposta
) {}
