package br.com.grupo.ClassInsight.controller;

import br.com.grupo.ClassInsight.dto.FeedbackCriacaoDTO;
import br.com.grupo.ClassInsight.dto.FeedbackDTO;
import br.com.grupo.ClassInsight.dto.RespostaFeedbackDTO;
import br.com.grupo.ClassInsight.model.StatusFeedback;
import br.com.grupo.ClassInsight.service.FeedbackService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/feedbacks")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FeedbackController {
    
    private final FeedbackService feedbackService;
    
    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }
    
    @PostMapping("/aluno/{alunoId}")
    public ResponseEntity<FeedbackDTO> criarFeedback(@PathVariable Long alunoId, @Valid @RequestBody FeedbackCriacaoDTO dto) {
        FeedbackDTO feedback = feedbackService.criarFeedback(alunoId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(feedback);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<FeedbackDTO> obterFeedbackPorId(@PathVariable Long id) {
        FeedbackDTO feedback = feedbackService.obterFeedbackPorId(id);
        return ResponseEntity.ok(feedback);
    }
    
    @GetMapping("/turma/{turmaId}")
    public ResponseEntity<List<FeedbackDTO>> listarFeedbackPorTurma(@PathVariable Long turmaId) {
        List<FeedbackDTO> feedbacks = feedbackService.listarFeedbackPorTurma(turmaId);
        return ResponseEntity.ok(feedbacks);
    }
    
    @GetMapping("/aluno/{alunoId}")
    public ResponseEntity<List<FeedbackDTO>> listarFeedbackPorAluno(@PathVariable Long alunoId) {
        List<FeedbackDTO> feedbacks = feedbackService.listarFeedbackPorAluno(alunoId);
        return ResponseEntity.ok(feedbacks);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<FeedbackDTO>> listarFeedbackPorStatus(@PathVariable StatusFeedback status) {
        List<FeedbackDTO> feedbacks = feedbackService.listarFeedbackPorStatus(status);
        return ResponseEntity.ok(feedbacks);
    }
    
    @GetMapping("/status/aberto")
    public ResponseEntity<List<FeedbackDTO>> listarFeedbackAberto() {
        List<FeedbackDTO> feedbacks = feedbackService.listarFeedbackAberto();
        return ResponseEntity.ok(feedbacks);
    }
    
    @PostMapping("/{feedbackId}/responder/{professorId}")
    public ResponseEntity<FeedbackDTO> responderFeedback(@PathVariable Long feedbackId, @PathVariable Long professorId, @Valid @RequestBody RespostaFeedbackDTO dto) {
        FeedbackDTO feedback = feedbackService.responderFeedback(feedbackId, professorId, dto);
        return ResponseEntity.ok(feedback);
    }
    
    @PutMapping("/{feedbackId}/fechar")
    public ResponseEntity<FeedbackDTO> fecharFeedback(@PathVariable Long feedbackId) {
        FeedbackDTO feedback = feedbackService.fecharFeedback(feedbackId);
        return ResponseEntity.ok(feedback);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarFeedback(@PathVariable Long id) {
        feedbackService.deletarFeedback(id);
        return ResponseEntity.noContent().build();
    }
}
