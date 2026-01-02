package br.com.grupo.ClassInsight.dto;

import br.com.grupo.ClassInsight.model.Urgencia;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvaliacaoDTO {

    private UUID id;

    @NotBlank(message = "Descrição é obrigatória")
    @Size(min = 10, max = 1000, message = "Descrição deve ter entre 10 e 1000 caracteres")
    private String descricao;

    @Min(value = 0, message = "Nota deve ser no mínimo 0")
    @Max(value = 10, message = "Nota deve ser no máximo 10")
    private Integer nota;

    private Urgencia urgencia;

    private LocalDateTime dataEnvio;

    private Boolean notificacaoEnviada;

    private Boolean processado;

    private String mensagem;
}
