package br.com.grupo.ClassInsight.service;

import br.com.grupo.ClassInsight.dto.RelatorioSemanalDTO;
import br.com.grupo.ClassInsight.model.Avaliacao;
import br.com.grupo.ClassInsight.model.Urgencia;
import br.com.grupo.ClassInsight.repository.AvaliacaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RelatorioService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final NotificationService notificationService;

    public RelatorioSemanalDTO gerarRelatorioSemanal() {
        log.info("Gerando relatório semanal");

        LocalDateTime dataFim = LocalDateTime.now();
        LocalDateTime dataInicio = dataFim.minusDays(7);

        return gerarRelatorioPeriodo(dataInicio, dataFim);
    }

    public RelatorioSemanalDTO gerarRelatorioPeriodo(LocalDateTime dataInicio, LocalDateTime dataFim) {
        log.info("Gerando relatório para período: {} a {}", dataInicio, dataFim);

        Long totalAvaliacoes = avaliacaoRepository.contarPorPeriodo(dataInicio, dataFim);
        Double mediaGeral = avaliacaoRepository.calcularMediaPorPeriodo(dataInicio, dataFim);

        Map<String, Long> avaliacoesPorDia = calcularAvaliacoesPorDia(dataInicio, dataFim);
        Map<Urgencia, Long> quantidadePorUrgencia = calcularQuantidadePorUrgencia(dataInicio, dataFim);
        List<RelatorioSemanalDTO.AvaliacaoResumoDTO> feedbacksCriticos = encontrarFeedbacksCriticos(dataInicio, dataFim);

        String periodoFormatado = formatarPeriodo(dataInicio, dataFim);

        RelatorioSemanalDTO relatorio = RelatorioSemanalDTO.builder()
                .dataInicio(dataInicio)
                .dataFim(dataFim)
                .totalAvaliacoes(totalAvaliacoes != null ? totalAvaliacoes : 0L)
                .mediaGeral(mediaGeral != null ? mediaGeral : 0.0)
                .avaliacoesPorDia(avaliacoesPorDia)
                .quantidadePorUrgencia(quantidadePorUrgencia)
                .feedbacksCriticos(feedbacksCriticos)
                .periodoFormatado(periodoFormatado)
                .build();

        log.info("Relatório gerado com sucesso: {} avaliações, média: {}", 
                relatorio.getTotalAvaliacoes(), relatorio.getMediaGeral());

        return relatorio;
    }

    private Map<String, Long> calcularAvaliacoesPorDia(LocalDateTime dataInicio, LocalDateTime dataFim) {
        List<Object[]> resultados = avaliacaoRepository.contarPorDiaNoPeriodo(dataInicio, dataFim);
        
        return resultados.stream()
                .collect(Collectors.toMap(
                        resultado -> ((java.sql.Date) resultado[0]).toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM")),
                        resultado -> (Long) resultado[1]
                ));
    }

    private Map<Urgencia, Long> calcularQuantidadePorUrgencia(LocalDateTime dataInicio, LocalDateTime dataFim) {
        Map<Urgencia, Long> quantidadePorUrgencia = new HashMap<>();
        
        for (Urgencia urgencia : Urgencia.values()) {
            Long quantidade = avaliacaoRepository.contarPorUrgenciaEPeriodo(urgencia, dataInicio, dataFim);
            quantidadePorUrgencia.put(urgencia, quantidade != null ? quantidade : 0L);
        }
        
        return quantidadePorUrgencia;
    }

    private List<RelatorioSemanalDTO.AvaliacaoResumoDTO> encontrarFeedbacksCriticos(LocalDateTime dataInicio, LocalDateTime dataFim) {
        List<Avaliacao> criticos = avaliacaoRepository.encontrarPorUrgenciaEPeriodo(
                Urgencia.CRITICO, dataInicio, dataFim);

        return criticos.stream()
                .limit(10)
                .map(avaliacao -> RelatorioSemanalDTO.AvaliacaoResumoDTO.builder()
                        .id(avaliacao.getId())
                        .descricao(avaliacao.getDescricao())
                        .nota(avaliacao.getNota())
                        .dataEnvio(avaliacao.getDataEnvio())
                        .build())
                .collect(Collectors.toList());
    }

    private String formatarPeriodo(LocalDateTime dataInicio, LocalDateTime dataFim) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return String.format("%s a %s", 
                dataInicio.format(formatter), 
                dataFim.format(formatter));
    }

    public void enviarRelatorioSemanalPorEmail() {
        try {
            log.info("Iniciando envio de relatório semanal por e-mail");
            
            RelatorioSemanalDTO relatorio = gerarRelatorioSemanal();
            String htmlRelatorio = gerarHtmlRelatorio(relatorio);
            
            notificationService.enviarRelatorioSemanal(htmlRelatorio);
            
            log.info("Relatório semanal enviado com sucesso por e-mail");
        } catch (Exception e) {
            log.error("Erro ao enviar relatório semanal por e-mail", e);
        }
    }

    private String gerarHtmlRelatorio(RelatorioSemanalDTO relatorio) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><style>");
        html.append("body{font-family:Arial,sans-serif;margin:20px;}");
        html.append(".header{background-color:#f8f9fa;padding:20px;border-radius:5px;margin-bottom:20px;}");
        html.append(".stats{display:flex;justify-content:space-between;margin-bottom:20px;}");
        html.append(".stat-box{background-color:#e9ecef;padding:15px;border-radius:5px;text-align:center;flex:1;margin:0 10px;}");
        html.append(".table{width:100%;border-collapse:collapse;margin-bottom:20px;}");
        html.append(".table th,.table td{border:1px solid #ddd;padding:8px;text-align:left;}");
        html.append(".table th{background-color:#f8f9fa;}");
        html.append(".critico{color:#dc3545;font-weight:bold;}");
        html.append(".medio{color:#ffc107;font-weight:bold;}");
        html.append(".positivo{color:#28a745;font-weight:bold;}");
        html.append("</style></head><body>");

        html.append("<div class='header'>");
        html.append("<h1>📊 Relatório Semanal de Feedbacks</h1>");
        html.append("<p><strong>Período:</strong> ").append(relatorio.getPeriodoFormatado()).append("</p>");
        html.append("</div>");

        html.append("<div class='stats'>");
        html.append("<div class='stat-box'><h3>").append(relatorio.getTotalAvaliacoes()).append("</h3><p>Total de Avaliações</p></div>");
        html.append("<div class='stat-box'><h3>").append(String.format("%.1f", relatorio.getMediaGeral())).append("</h3><p>Média Geral</p></div>");
        html.append("</div>");

        html.append("<h2>📈 Avaliações por Urgência</h2>");
        html.append("<table class='table'>");
        html.append("<tr><th>Urgência</th><th>Quantidade</th></tr>");
        for (Map.Entry<Urgencia, Long> entry : relatorio.getQuantidadePorUrgencia().entrySet()) {
            String cssClass = entry.getKey() == Urgencia.CRITICO ? "critico" : 
                              entry.getKey() == Urgencia.MEDIO ? "medio" : "positivo";
            html.append("<tr><td class='").append(cssClass).append("'>").append(entry.getKey())
                .append("</td><td>").append(entry.getValue()).append("</td></tr>");
        }
        html.append("</table>");

        html.append("<h2>📅 Avaliações por Dia</h2>");
        html.append("<table class='table'>");
        html.append("<tr><th>Dia</th><th>Quantidade</th></tr>");
        for (Map.Entry<String, Long> entry : relatorio.getAvaliacoesPorDia().entrySet()) {
            html.append("<tr><td>").append(entry.getKey()).append("</td><td>").append(entry.getValue()).append("</td></tr>");
        }
        html.append("</table>");

        if (!relatorio.getFeedbacksCriticos().isEmpty()) {
            html.append("<h2>🚨 Top 10 Feedbacks Críticos</h2>");
            html.append("<table class='table'>");
            html.append("<tr><th>ID</th><th>Descrição</th><th>Nota</th><th>Data</th></tr>");
            for (RelatorioSemanalDTO.AvaliacaoResumoDTO critico : relatorio.getFeedbacksCriticos()) {
                html.append("<tr>");
                html.append("<td>").append(critico.getId().toString().substring(0, 8)).append("...</td>");
                html.append("<td>").append(critico.getDescricao().substring(0, Math.min(50, critico.getDescricao().length()))).append("...</td>");
                html.append("<td class='critico'>").append(critico.getNota()).append("</td>");
                html.append("<td>").append(critico.getDataEnvio().format(DateTimeFormatter.ofPattern("dd/MM HH:mm"))).append("</td>");
                html.append("</tr>");
            }
            html.append("</table>");
        }

        html.append("<div style='margin-top:30px;text-align:center;color:#6c757d;'>");
        html.append("<p>Relatório gerado em ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))).append("</p>");
        html.append("<p>Sistema ClassInsight</p>");
        html.append("</div>");

        html.append("</body></html>");
        return html.toString();
    }
}
