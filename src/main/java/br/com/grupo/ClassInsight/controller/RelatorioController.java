package br.com.grupo.ClassInsight.controller;

import br.com.grupo.ClassInsight.dto.RelatorioSemanalDTO;
import br.com.grupo.ClassInsight.service.RelatorioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/relatorio")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Relatórios", description = "API para geração de relatórios e estatísticas")
public class RelatorioController {

    private final RelatorioService relatorioService;

    @GetMapping("/semanal")
    @Operation(
        summary = "Gerar relatório semanal",
        description = "Gera um relatório completo com estatísticas dos últimos 7 dias"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Relatório gerado com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RelatorioSemanalDTO.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "dataInicio": "2024-01-08T10:30:00",
                        "dataFim": "2024-01-15T10:30:00",
                        "totalAvaliacoes": 150,
                        "mediaGeral": 7.8,
                        "periodoFormatado": "08/01/2024 a 15/01/2024",
                        "avaliacoesPorDia": {
                            "08/01": 20,
                            "09/01": 25,
                            "10/01": 18,
                            "11/01": 22,
                            "12/01": 30,
                            "13/01": 15,
                            "14/01": 20
                        },
                        "quantidadePorUrgencia": {
                            "CRITICO": 15,
                            "MEDIO": 45,
                            "POSITIVO": 90
                        },
                        "feedbacksCriticos": [
                            {
                                "id": "550e8400-e29b-41d4-a716-446655440000",
                                "descricao": "Aula muito confusa...",
                                "nota": 2,
                                "dataEnvio": "2024-01-14T15:30:00"
                            }
                        ]
                    }
                    """
                )
            )
        ),
        @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    public ResponseEntity<RelatorioSemanalDTO> gerarRelatorioSemanal() {
        log.info("Gerando relatório semanal");
        
        RelatorioSemanalDTO relatorio = relatorioService.gerarRelatorioSemanal();
        
        return ResponseEntity.ok(relatorio);
    }

    @GetMapping("/periodo")
    @Operation(
        summary = "Gerar relatório por período",
        description = "Gera um relatório completo para um período específico"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Parâmetros inválidos")
    })
    public ResponseEntity<RelatorioSemanalDTO> gerarRelatorioPeriodo(
            @Parameter(description = "Data início do período (formato: yyyy-MM-ddTHH:mm:ss)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            
            @Parameter(description = "Data fim do período (formato: yyyy-MM-ddTHH:mm:ss)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim) {
        
        log.info("Gerando relatório para período: {} a {}", dataInicio, dataFim);
        
        if (dataInicio.isAfter(dataFim)) {
            throw new IllegalArgumentException("Data início não pode ser posterior à data fim");
        }
        
        RelatorioSemanalDTO relatorio = relatorioService.gerarRelatorioPeriodo(dataInicio, dataFim);
        
        return ResponseEntity.ok(relatorio);
    }

    @PostMapping("/semanal/enviar")
    @Operation(
        summary = "Enviar relatório semanal por e-mail",
        description = "Gera e envia o relatório semanal por e-mail para os administradores"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Relatório enviado com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro ao enviar e-mail")
    })
    public ResponseEntity<String> enviarRelatorioSemanalPorEmail() {
        log.info("Enviando relatório semanal por e-mail");
        
        try {
            relatorioService.enviarRelatorioSemanalPorEmail();
            return ResponseEntity.ok("Relatório semanal enviado com sucesso por e-mail");
        } catch (Exception e) {
            log.error("Erro ao enviar relatório semanal por e-mail", e);
            return ResponseEntity.internalServerError()
                    .body("Erro ao enviar relatório: " + e.getMessage());
        }
    }
}
