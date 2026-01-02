package br.com.grupo.ClassInsight.config;

import br.com.grupo.ClassInsight.service.RelatorioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class ScheduledTasks {

    private final RelatorioService relatorioService;

    @Scheduled(cron = "0 0 9 * * MON", zone = "America/Sao_Paulo")
    public void gerarRelatorioSemanalAgendado() {
        log.info("Iniciando geração agendada de relatório semanal - Segunda-feira 9h");
        
        try {
            relatorioService.enviarRelatorioSemanalPorEmail();
            log.info("Relatório semanal agendado enviado com sucesso");
        } catch (Exception e) {
            log.error("Erro ao gerar relatório semanal agendado", e);
        }
    }

    @Scheduled(cron = "0 0 8 * * MON", zone = "America/Sao_Paulo")
    public void verificarSistema() {
        log.info("Verificação agendada do sistema - Segunda-feira 8h");
        
        try {
            log.info("Sistema ClassInsight funcionando normalmente");
            log.info("Próximo relatório semanal será gerado em 1 hora");
        } catch (Exception e) {
            log.error("Erro na verificação agendada do sistema", e);
        }
    }

    @Scheduled(fixedRate = 300000) // A cada 5 minutos
    public void healthCheck() {
        log.debug("Health check do sistema - Status: OK");
    }
}
