package br.com.grupo.ClassInsight.service;

import br.com.grupo.ClassInsight.model.Feedback;
import br.com.grupo.ClassInsight.model.Turma;
import br.com.grupo.ClassInsight.model.Usuario;
import br.com.grupo.ClassInsight.model.StatusFeedback;
import br.com.grupo.ClassInsight.dto.FeedbackCriacaoDTO;
import br.com.grupo.ClassInsight.dto.FeedbackDTO;
import br.com.grupo.ClassInsight.dto.RespostaFeedbackDTO;
import br.com.grupo.ClassInsight.repository.FeedbackRepository;
import br.com.grupo.ClassInsight.repository.TurmaRepository;
import br.com.grupo.ClassInsight.repository.UsuarioRepository;
import br.com.grupo.ClassInsight.exception.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FeedbackService {
    
    private final FeedbackRepository feedbackRepository;
    private final TurmaRepository turmaRepository;
    private final UsuarioRepository usuarioRepository;
    
    public FeedbackService(FeedbackRepository feedbackRepository, TurmaRepository turmaRepository, UsuarioRepository usuarioRepository) {
        this.feedbackRepository = feedbackRepository;
        this.turmaRepository = turmaRepository;
        this.usuarioRepository = usuarioRepository;
    }
    
    public FeedbackDTO criarFeedback(Long alunoId, FeedbackCriacaoDTO dto) {
        Usuario aluno = usuarioRepository.findById(alunoId)
            .orElseThrow(() -> new EntityNotFoundException("Aluno não encontrado com ID: " + alunoId));
        
        Turma turma = turmaRepository.findById(dto.turmaId())
            .orElseThrow(() -> new EntityNotFoundException("Turma não encontrada com ID: " + dto.turmaId()));
        
        Feedback feedback = new Feedback();
        feedback.setTurma(turma);
        feedback.setAluno(aluno);
        feedback.setTitulo(dto.titulo());
        feedback.setConteudo(dto.conteudo());
        feedback.setTipoFeedback(dto.tipoFeedback());
        feedback.setStatus(StatusFeedback.ABERTO);
        
        Feedback feedbackSalvo = feedbackRepository.save(feedback);
        return converterParaDTO(feedbackSalvo);
    }
    
    public FeedbackDTO obterFeedbackPorId(Long id) {
        Feedback feedback = feedbackRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Feedback não encontrado com ID: " + id));
        return converterParaDTO(feedback);
    }
    
    public List<FeedbackDTO> listarFeedbackPorTurma(Long turmaId) {
        Turma turma = turmaRepository.findById(turmaId)
            .orElseThrow(() -> new EntityNotFoundException("Turma não encontrada com ID: " + turmaId));
        return feedbackRepository.findByTurma(turma).stream()
            .map(this::converterParaDTO)
            .collect(Collectors.toList());
    }
    
    public List<FeedbackDTO> listarFeedbackPorAluno(Long alunoId) {
        Usuario aluno = usuarioRepository.findById(alunoId)
            .orElseThrow(() -> new EntityNotFoundException("Aluno não encontrado com ID: " + alunoId));
        return feedbackRepository.findByAluno(aluno).stream()
            .map(this::converterParaDTO)
            .collect(Collectors.toList());
    }
    
    public List<FeedbackDTO> listarFeedbackPorStatus(StatusFeedback status) {
        return feedbackRepository.findByStatus(status).stream()
            .map(this::converterParaDTO)
            .collect(Collectors.toList());
    }
    
    public List<FeedbackDTO> listarFeedbackAberto() {
        return listarFeedbackPorStatus(StatusFeedback.ABERTO);
    }
    
    public FeedbackDTO responderFeedback(Long feedbackId, Long professorId, RespostaFeedbackDTO dto) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
            .orElseThrow(() -> new EntityNotFoundException("Feedback não encontrado com ID: " + feedbackId));
        
        if (feedback.getStatus() == StatusFeedback.FECHADO) {
            throw new IllegalArgumentException("Não é possível responder a um feedback fechado");
        }
        
        Usuario professor = usuarioRepository.findById(professorId)
            .orElseThrow(() -> new EntityNotFoundException("Professor não encontrado com ID: " + professorId));
        
        feedback.setResposta(dto.resposta());
        feedback.setRespondidoPor(professor);
        feedback.setStatus(StatusFeedback.RESPONDIDO);
        feedback.setDataResposta(LocalDateTime.now());
        
        Feedback feedbackAtualizado = feedbackRepository.save(feedback);
        return converterParaDTO(feedbackAtualizado);
    }
    
    public FeedbackDTO fecharFeedback(Long feedbackId) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
            .orElseThrow(() -> new EntityNotFoundException("Feedback não encontrado com ID: " + feedbackId));
        
        feedback.setStatus(StatusFeedback.FECHADO);
        Feedback feedbackAtualizado = feedbackRepository.save(feedback);
        return converterParaDTO(feedbackAtualizado);
    }
    
    public void deletarFeedback(Long id) {
        feedbackRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Feedback não encontrado com ID: " + id));
        feedbackRepository.deleteById(id);
    }
    
    private FeedbackDTO converterParaDTO(Feedback feedback) {
        return new FeedbackDTO(
            feedback.getId(),
            feedback.getTurma().getId(),
            feedback.getAluno().getId(),
            feedback.getTitulo(),
            feedback.getConteudo(),
            feedback.getTipoFeedback(),
            feedback.getStatus(),
            feedback.getDataCriacao(),
            feedback.getResposta(),
            feedback.getDataResposta()
        );
    }
}
