package br.com.grupo.ClassInsight.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Tipos de usuários do sistema")
public enum TipoUsuario {
    @Schema(description = "Usuário do tipo aluno")
    ALUNO,
    
    @Schema(description = "Usuário do tipo professor")
    PROFESSOR,
    
    @Schema(description = "Usuário do tipo administrador")
    ADMINISTRADOR
}
