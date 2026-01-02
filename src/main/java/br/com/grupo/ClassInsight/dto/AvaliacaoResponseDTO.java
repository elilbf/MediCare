package br.com.grupo.ClassInsight.dto;

import br.com.grupo.ClassInsight.model.Urgencia;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvaliacaoResponseDTO {

    private UUID id;
    private String descricao;
    private Integer nota;
    private Urgencia urgencia;
    private LocalDateTime dataEnvio;
    private Boolean notificacaoEnviada;
    private Boolean processado;
    private String mensagem;

    public static AvaliacaoResponseDTO fromEntity(br.com.grupo.ClassInsight.model.Avaliacao avaliacao, String mensagem) {
        return AvaliacaoResponseDTO.builder()
                .id(avaliacao.getId())
                .descricao(avaliacao.getDescricao())
                .nota(avaliacao.getNota())
                .urgencia(avaliacao.getUrgencia())
                .dataEnvio(avaliacao.getDataEnvio())
                .notificacaoEnviada(avaliacao.getNotificacaoEnviada())
                .processado(avaliacao.getProcessado())
                .mensagem(mensagem)
                .build();
    }
}
