package br.com.grupo.ClassInsight.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Status possíveis para um feedback")
public enum StatusFeedback {
    @Schema(description = "Feedback aberto e aguardando análise")
    ABERTO,
    
    @Schema(description = "Feedback em análise pela equipe")
    EM_ANALISE,
    
    @Schema(description = "Feedback já respondido pelo professor")
    RESPONDIDO,
    
    @Schema(description = "Feedback fechado e concluído")
    FECHADO
}
