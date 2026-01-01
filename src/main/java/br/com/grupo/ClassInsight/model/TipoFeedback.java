package br.com.grupo.ClassInsight.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Tipos de feedbacks do sistema")
public enum TipoFeedback {
    @Schema(description = "Feedback do tipo dúvida")
    DUVIDA,
    
    @Schema(description = "Feedback do tipo sugestão")
    SUGESTAO,
    
    @Schema(description = "Feedback do tipo reclamação")
    RECLAMACAO,
    
    @Schema(description = "Feedback do tipo elogio")
    ELOGIO,
    
    @Schema(description = "Feedback de outro tipo")
    OUTRO
}
