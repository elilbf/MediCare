package br.com.grupo.ClassInsight.controller;

import br.com.grupo.ClassInsight.service.AvaliacaoService;
import br.com.grupo.ClassInsight.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notificacao")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Notificações", description = "API para gerenciamento de notificações por e-mail")
public class NotificacaoController {

    private final NotificationService notificationService;
    private final AvaliacaoService avaliacaoService;

    @PostMapping("/enviar/{avaliacaoId}")
    @Operation(
        summary = "Enviar notificação manual",
        description = "Envia uma notificação por e-mail para uma avaliação específica"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notificação enviada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Avaliação não encontrada"),
        @ApiResponse(responseCode = "500", description = "Erro ao enviar notificação")
    })
    public ResponseEntity<String> enviarNotificacaoManual(
            @Parameter(description = "ID da avaliação") 
            @PathVariable UUID avaliacaoId) {
        
        log.info("Recebida requisição para enviar notificação manual para avaliação ID: {}", avaliacaoId);
        
        try {
            var avaliacao = avaliacaoService.buscarAvaliacaoPorId(avaliacaoId);
            
            notificationService.enviarNotificacaoManual(
                br.com.grupo.ClassInsight.model.Avaliacao.builder()
                    .id(avaliacao.getId())
                    .descricao(avaliacao.getDescricao())
                    .nota(avaliacao.getNota())
                    .urgencia(avaliacao.getUrgencia())
                    .dataEnvio(avaliacao.getDataEnvio())
                    .notificacaoEnviada(avaliacao.getNotificacaoEnviada())
                    .build()
            );
            
            avaliacaoService.marcarNotificacaoEnviada(avaliacaoId);
            
            return ResponseEntity.ok("Notificação enviada com sucesso para avaliação ID: " + avaliacaoId);
            
        } catch (Exception e) {
            log.error("Erro ao enviar notificação manual para avaliação ID: {}", avaliacaoId, e);
            return ResponseEntity.internalServerError()
                    .body("Erro ao enviar notificação: " + e.getMessage());
        }
    }

    @PostMapping("/testar")
    @Operation(
        summary = "Testar sistema de notificação",
        description = "Envia um e-mail de teste para verificar se o sistema está funcionando"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "E-mail de teste enviado com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro ao enviar e-mail de teste")
    })
    public ResponseEntity<String> testarSistemaNotificacao() {
        log.info("Enviando e-mail de teste do sistema de notificação");
        
        try {
            notificationService.enviarMultiplosEmails(
                java.util.List.of("admin@classinsight.com"),
                "Teste do Sistema de Notificação",
                "Este é um e-mail de teste para verificar que o sistema de notificação está funcionando corretamente.\n\n" +
                "Data do teste: " + java.time.LocalDateTime.now() + "\n" +
                "Sistema: ClassInsight\n" +
                "Status: ✅ Funcionando"
            );
            
            return ResponseEntity.ok("E-mail de teste enviado com sucesso");
            
        } catch (Exception e) {
            log.error("Erro ao enviar e-mail de teste", e);
            return ResponseEntity.internalServerError()
                    .body("Erro ao enviar e-mail de teste: " + e.getMessage());
        }
    }
}
