package br.com.grupo.ClassInsight.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvaliacaoRequestDTO {

    @NotBlank(message = "Descrição é obrigatória")
    @Size(min = 10, max = 1000, message = "Descrição deve ter entre 10 e 1000 caracteres")
    private String descricao;

    @Min(value = 0, message = "Nota deve ser no mínimo 0")
    @Max(value = 10, message = "Nota deve ser no máximo 10")
    private Integer nota;
}
