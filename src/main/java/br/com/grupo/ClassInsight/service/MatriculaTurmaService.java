package br.com.grupo.ClassInsight.service;

import br.com.grupo.ClassInsight.model.MatriculaTurma;
import br.com.grupo.ClassInsight.model.Turma;
import br.com.grupo.ClassInsight.model.Usuario;
import br.com.grupo.ClassInsight.repository.MatriculaTurmaRepository;
import br.com.grupo.ClassInsight.repository.TurmaRepository;
import br.com.grupo.ClassInsight.repository.UsuarioRepository;
import br.com.grupo.ClassInsight.exception.ResourceNotFoundException;
import br.com.grupo.ClassInsight.exception.DuplicateResourceException;
import br.com.grupo.ClassInsight.exception.InvalidOperationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class MatriculaTurmaService {
    
    private final MatriculaTurmaRepository matriculaTurmaRepository;
    private final UsuarioRepository usuarioRepository;
    private final TurmaRepository turmaRepository;
    
    public MatriculaTurmaService(MatriculaTurmaRepository matriculaTurmaRepository, 
                                UsuarioRepository usuarioRepository, 
                                TurmaRepository turmaRepository) {
        this.matriculaTurmaRepository = matriculaTurmaRepository;
        this.usuarioRepository = usuarioRepository;
        this.turmaRepository = turmaRepository;
    }
    
    public MatriculaTurma matricularAluno(Long alunoId, Long turmaId) {
        Usuario aluno = usuarioRepository.findById(alunoId)
            .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado com ID: " + alunoId));
        
        Turma turma = turmaRepository.findById(turmaId)
            .orElseThrow(() -> new ResourceNotFoundException("Turma não encontrada com ID: " + turmaId));
        
        if (!turma.isAtiva()) {
            throw new InvalidOperationException("Turma não está ativa");
        }
        
        if (matriculaTurmaRepository.findByAlunoAndTurma(aluno, turma).isPresent()) {
            throw new DuplicateResourceException("Aluno já está matriculado nesta turma");
        }
        
        MatriculaTurma matricula = new MatriculaTurma();
        matricula.setAluno(aluno);
        matricula.setTurma(turma);
        matricula.setAtiva(true);
        
        return matriculaTurmaRepository.save(matricula);
    }
    
    public void desmatricular(Long alunoId, Long turmaId) {
        Usuario aluno = usuarioRepository.findById(alunoId)
            .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado com ID: " + alunoId));
        
        Turma turma = turmaRepository.findById(turmaId)
            .orElseThrow(() -> new ResourceNotFoundException("Turma não encontrada com ID: " + turmaId));
        
        MatriculaTurma matricula = matriculaTurmaRepository.findByAlunoAndTurma(aluno, turma)
            .orElseThrow(() -> new ResourceNotFoundException("Matrícula não encontrada"));
        
        matricula.setAtiva(false);
        matriculaTurmaRepository.save(matricula);
    }
    
    public List<MatriculaTurma> listarMatriculasPorAluno(Long alunoId) {
        Usuario aluno = usuarioRepository.findById(alunoId)
            .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado com ID: " + alunoId));
        return matriculaTurmaRepository.findByAlunoAndAtivaTrue(aluno);
    }
    
    public List<MatriculaTurma> listarMatriculasPorTurma(Long turmaId) {
        Turma turma = turmaRepository.findById(turmaId)
            .orElseThrow(() -> new ResourceNotFoundException("Turma não encontrada com ID: " + turmaId));
        return matriculaTurmaRepository.findByTurmaAndAtivaTrue(turma);
    }
    
    public boolean verificarMatricula(Long alunoId, Long turmaId) {
        Usuario aluno = usuarioRepository.findById(alunoId)
            .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado com ID: " + alunoId));
        
        Turma turma = turmaRepository.findById(turmaId)
            .orElseThrow(() -> new ResourceNotFoundException("Turma não encontrada com ID: " + turmaId));
        
        return matriculaTurmaRepository.findByAlunoAndTurma(aluno, turma)
            .map(MatriculaTurma::isAtiva)
            .orElse(false);
    }
}
