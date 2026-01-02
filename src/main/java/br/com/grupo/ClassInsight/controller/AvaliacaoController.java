package br.com.grupo.ClassInsight.controller;

import br.com.grupo.ClassInsight.dto.AvaliacaoRequestDTO;
import br.com.grupo.ClassInsight.dto.AvaliacaoResponseDTO;
import br.com.grupo.ClassInsight.model.Urgencia;
import br.com.grupo.ClassInsight.service.AvaliacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/avaliacao")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Avaliações", description = "API para gerenciamento de avaliações de feedback")
public class AvaliacaoController {

    private final AvaliacaoService avaliacaoService;

    @PostMapping
    @Operation(
        summary = "Criar nova avaliação",
        description = "Recebe uma nova avaliação de feedback e a classifica automaticamente por urgência"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Avaliação criada com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AvaliacaoResponseDTO.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "id": "550e8400-e29b-41d4-a716-446655440000",
                        "descricao": "Aula excelente, professor muito didático!",
                        "nota": 9,
                        "urgencia": "POSITIVO",
                        "dataEnvio": "2024-01-15T10:30:00",
                        "notificacaoEnviada": false,
                        "processado": false,
                        "mensagem": "Avaliação criada com sucesso"
                    }
                    """
                )
            )
        ),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    public ResponseEntity<AvaliacaoResponseDTO> criarAvaliacao(
            @Valid @RequestBody 
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Dados da avaliação",
                content = @Content(
                    examples = @ExampleObject(
                        value = """
                        {
                            "descricao": "Aula excelente, professor muito didático!",
                            "nota": 9
                        }
                        """
                    )
                )
            )
            AvaliacaoRequestDTO requestDTO) {
        
        log.info("Recebida requisição para criar avaliação com nota: {}", requestDTO.getNota());
        
        AvaliacaoResponseDTO response = avaliacaoService.criarAvaliacao(requestDTO);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(
        summary = "Listar avaliações",
        description = "Lista todas as avaliações com suporte a paginação e filtros"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de avaliações retornada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Parâmetros inválidos")
    })
    public ResponseEntity<Page<AvaliacaoResponseDTO>> listarAvaliacoes(
            @Parameter(description = "Número da página (0-based)") 
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Tamanho da página") 
            @RequestParam(defaultValue = "10") int size,
            
            @Parameter(description = "Filtrar por urgência") 
            @RequestParam(required = false) Urgencia urgencia,
            
            @Parameter(description = "Data início do período (formato: yyyy-MM-ddTHH:mm:ss)") 
            @RequestParam(required = false) LocalDateTime dataInicio,
            
            @Parameter(description = "Data fim do período (formato: yyyy-MM-ddTHH:mm:ss)") 
            @RequestParam(required = false) LocalDateTime dataFim) {
        
        log.info("Listando avaliações - página: {}, tamanho: {}, urgência: {}, período: {} a {}", 
                page, size, urgencia, dataInicio, dataFim);

        Pageable pageable = PageRequest.of(page, size, Sort.by("dataEnvio").descending());
        Page<AvaliacaoResponseDTO> avaliacoes = avaliacaoService.listarAvaliacoes(pageable, urgencia, dataInicio, dataFim);

        return ResponseEntity.ok(avaliacoes);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Buscar avaliação por ID",
        description = "Retorna uma avaliação específica pelo seu UUID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Avaliação encontrada"),
        @ApiResponse(responseCode = "404", description = "Avaliação não encontrada")
    })
    public ResponseEntity<AvaliacaoResponseDTO> buscarAvaliacaoPorId(
            @Parameter(description = "ID da avaliação") 
            @PathVariable UUID id) {
        
        log.info("Buscando avaliação por ID: {}", id);
        
        AvaliacaoResponseDTO avaliacao = avaliacaoService.buscarAvaliacaoPorId(id);
        
        return ResponseEntity.ok(avaliacao);
    }
}
