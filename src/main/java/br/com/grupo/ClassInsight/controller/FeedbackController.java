package br.com.grupo.ClassInsight.controller;

import br.com.grupo.ClassInsight.dto.FeedbackCriacaoDTO;
import br.com.grupo.ClassInsight.dto.FeedbackDTO;
import br.com.grupo.ClassInsight.dto.RespostaFeedbackDTO;
import br.com.grupo.ClassInsight.model.StatusFeedback;
import br.com.grupo.ClassInsight.service.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "Feedbacks", description = "API para gerenciamento de feedbacks educacionais")
@RestController
@RequestMapping("/api/feedbacks")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FeedbackController {
    
    private final FeedbackService feedbackService;
    
    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }
    
    @Operation(summary = "Criar novo feedback", description = "Cria um novo feedback para um aluno específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Feedback criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Aluno não encontrado")
    })
    @PostMapping("/aluno/{alunoId}")
    public ResponseEntity<FeedbackDTO> criarFeedback(
            @Parameter(description = "ID do aluno") @PathVariable Long alunoId, 
            @Valid @RequestBody FeedbackCriacaoDTO dto) {
        FeedbackDTO feedback = feedbackService.criarFeedback(alunoId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(feedback);
    }
    
    @Operation(summary = "Obter feedback por ID", description = "Retorna um feedback específico pelo seu ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Feedback encontrado"),
        @ApiResponse(responseCode = "404", description = "Feedback não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<FeedbackDTO> obterFeedbackPorId(
            @Parameter(description = "ID do feedback") @PathVariable Long id) {
        FeedbackDTO feedback = feedbackService.obterFeedbackPorId(id);
        return ResponseEntity.ok(feedback);
    }
    
    @Operation(summary = "Listar feedbacks por turma", description = "Retorna todos os feedbacks de uma turma específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de feedbacks retornada"),
        @ApiResponse(responseCode = "404", description = "Turma não encontrada")
    })
    @GetMapping("/turma/{turmaId}")
    public ResponseEntity<List<FeedbackDTO>> listarFeedbackPorTurma(
            @Parameter(description = "ID da turma") @PathVariable Long turmaId) {
        List<FeedbackDTO> feedbacks = feedbackService.listarFeedbackPorTurma(turmaId);
        return ResponseEntity.ok(feedbacks);
    }
    
    @Operation(summary = "Listar feedbacks por aluno", description = "Retorna todos os feedbacks de um aluno específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de feedbacks retornada"),
        @ApiResponse(responseCode = "404", description = "Aluno não encontrado")
    })
    @GetMapping("/aluno/{alunoId}")
    public ResponseEntity<List<FeedbackDTO>> listarFeedbackPorAluno(
            @Parameter(description = "ID do aluno") @PathVariable Long alunoId) {
        List<FeedbackDTO> feedbacks = feedbackService.listarFeedbackPorAluno(alunoId);
        return ResponseEntity.ok(feedbacks);
    }
    
    @Operation(summary = "Listar feedbacks por status", description = "Retorna todos os feedbacks com um status específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de feedbacks retornada")
    })
    @GetMapping("/status/{status}")
    public ResponseEntity<List<FeedbackDTO>> listarFeedbackPorStatus(
            @Parameter(description = "Status do feedback") @PathVariable StatusFeedback status) {
        List<FeedbackDTO> feedbacks = feedbackService.listarFeedbackPorStatus(status);
        return ResponseEntity.ok(feedbacks);
    }
    
    @Operation(summary = "Listar feedbacks abertos", description = "Retorna todos os feedbacks com status ABERTO")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de feedbacks abertos retornada")
    })
    @GetMapping("/status/aberto")
    public ResponseEntity<List<FeedbackDTO>> listarFeedbackAberto() {
        List<FeedbackDTO> feedbacks = feedbackService.listarFeedbackAberto();
        return ResponseEntity.ok(feedbacks);
    }
    
    @Operation(summary = "Responder feedback", description = "Adiciona uma resposta de professor a um feedback")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Feedback respondido com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Feedback ou professor não encontrado")
    })
    @PostMapping("/{feedbackId}/responder/{professorId}")
    public ResponseEntity<FeedbackDTO> responderFeedback(
            @Parameter(description = "ID do feedback") @PathVariable Long feedbackId, 
            @Parameter(description = "ID do professor") @PathVariable Long professorId, 
            @Valid @RequestBody RespostaFeedbackDTO dto) {
        FeedbackDTO feedback = feedbackService.responderFeedback(feedbackId, professorId, dto);
        return ResponseEntity.ok(feedback);
    }
    
    @Operation(summary = "Fechar feedback", description = "Fecha um feedback, impedindo novas respostas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Feedback fechado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Feedback não encontrado")
    })
    @PutMapping("/{feedbackId}/fechar")
    public ResponseEntity<FeedbackDTO> fecharFeedback(
            @Parameter(description = "ID do feedback") @PathVariable Long feedbackId) {
        FeedbackDTO feedback = feedbackService.fecharFeedback(feedbackId);
        return ResponseEntity.ok(feedback);
    }
    
    @Operation(summary = "Deletar feedback", description = "Remove um feedback do sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Feedback deletado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Feedback não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarFeedback(
            @Parameter(description = "ID do feedback") @PathVariable Long id) {
        feedbackService.deletarFeedback(id);
        return ResponseEntity.noContent().build();
    }
}
