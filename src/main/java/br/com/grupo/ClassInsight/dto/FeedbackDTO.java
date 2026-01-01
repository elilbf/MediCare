package br.com.grupo.ClassInsight.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import br.com.grupo.ClassInsight.model.TipoFeedback;
import br.com.grupo.ClassInsight.model.StatusFeedback;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Representação de um feedback com todos os seus dados")
public record FeedbackDTO(
    @Schema(description = "ID único do feedback", example = "1")
    Long id,
    
    @Schema(description = "ID da turma associada ao feedback", example = "10")
    @JsonProperty("turma_id")
    Long turmaId,
    
    @Schema(description = "ID do aluno que criou o feedback", example = "5")
    @JsonProperty("aluno_id")
    Long alunoId,
    
    @Schema(description = "Título do feedback", example = "Dúvida sobre conteúdo da aula")
    String titulo,
    
    @Schema(description = "Conteúdo detalhado do feedback", example = "Não consegui entender o conceito de polimorfismo")
    String conteudo,
    
    @Schema(description = "Tipo do feedback", example = "DUVIDA")
    @JsonProperty("tipo_feedback")
    TipoFeedback tipoFeedback,
    
    @Schema(description = "Status atual do feedback", example = "ABERTO")
    StatusFeedback status,
    
    @Schema(description = "Data e hora de criação do feedback")
    @JsonProperty("data_criacao")
    LocalDateTime dataCriacao,
    
    @Schema(description = "Resposta do professor (se houver)", example = "Vou explicar com exemplos práticos na próxima aula")
    String resposta,
    
    @Schema(description = "Data e hora da resposta do professor")
    @JsonProperty("data_resposta")
    LocalDateTime dataResposta
) {}
