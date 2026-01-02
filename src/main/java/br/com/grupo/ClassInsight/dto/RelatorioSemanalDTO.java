package br.com.grupo.ClassInsight.dto;

import br.com.grupo.ClassInsight.model.Urgencia;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RelatorioSemanalDTO {

    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private Long totalAvaliacoes;
    private Double mediaGeral;
    private Map<String, Long> avaliacoesPorDia;
    private Map<Urgencia, Long> quantidadePorUrgencia;
    private List<AvaliacaoResumoDTO> feedbacksCriticos;
    private String periodoFormatado;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AvaliacaoResumoDTO {
        private UUID id;
        private String descricao;
        private Integer nota;
        private LocalDateTime dataEnvio;
    }
}
