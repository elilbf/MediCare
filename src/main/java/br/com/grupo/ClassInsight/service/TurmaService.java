package br.com.grupo.ClassInsight.service;

import br.com.grupo.ClassInsight.model.Turma;
import br.com.grupo.ClassInsight.model.Usuario;
import br.com.grupo.ClassInsight.model.TipoUsuario;
import br.com.grupo.ClassInsight.dto.TurmaCriacaoDTO;
import br.com.grupo.ClassInsight.dto.TurmaDTO;
import br.com.grupo.ClassInsight.repository.TurmaRepository;
import br.com.grupo.ClassInsight.repository.UsuarioRepository;
import br.com.grupo.ClassInsight.exception.ResourceNotFoundException;
import br.com.grupo.ClassInsight.exception.InvalidOperationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class TurmaService {
    
    private final TurmaRepository turmaRepository;
    private final UsuarioRepository usuarioRepository;
    
    public TurmaService(TurmaRepository turmaRepository, UsuarioRepository usuarioRepository) {
        this.turmaRepository = turmaRepository;
        this.usuarioRepository = usuarioRepository;
    }
    
    public TurmaDTO criarTurma(TurmaCriacaoDTO dto) {
        Usuario professor = usuarioRepository.findById(dto.professorId())
            .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado com ID: " + dto.professorId()));
        
        if (professor.getTipoUsuario() != TipoUsuario.PROFESSOR) {
            throw new InvalidOperationException("Usuário com ID " + dto.professorId() + " não é um professor");
        }
        
        Turma turma = new Turma();
        turma.setNome(dto.nome());
        turma.setDescricao(dto.descricao());
        turma.setProfessor(professor);
        turma.setCodigoTurma(gerarCodigoTurma());
        turma.setAtiva(true);
        
        Turma turmaSalva = turmaRepository.save(turma);
        return converterParaDTO(turmaSalva);
    }
    
    public TurmaDTO obterTurmaPorId(Long id) {
        Turma turma = turmaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Turma não encontrada com ID: " + id));
        return converterParaDTO(turma);
    }
    
    public TurmaDTO obterTurmaPorCodigo(String codigo) {
        Turma turma = turmaRepository.findByCodigoTurma(codigo)
            .orElseThrow(() -> new ResourceNotFoundException("Turma não encontrada com código: " + codigo));
        return converterParaDTO(turma);
    }
    
    public List<TurmaDTO> listarTodas() {
        return turmaRepository.findByAtivaTrue().stream()
            .map(this::converterParaDTO)
            .collect(Collectors.toList());
    }
    
    public List<TurmaDTO> listarPorProfessor(Long professorId) {
        Usuario professor = usuarioRepository.findById(professorId)
            .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado com ID: " + professorId));
        return turmaRepository.findByProfessor(professor).stream()
            .filter(Turma::isAtiva)
            .map(this::converterParaDTO)
            .collect(Collectors.toList());
    }
    
    public TurmaDTO atualizarTurma(Long id, TurmaCriacaoDTO dto) {
        Turma turma = turmaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Turma não encontrada com ID: " + id));
        
        Usuario professor = usuarioRepository.findById(dto.professorId())
            .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado com ID: " + dto.professorId()));
        
        turma.setNome(dto.nome());
        turma.setDescricao(dto.descricao());
        turma.setProfessor(professor);
        
        Turma turmaAtualizada = turmaRepository.save(turma);
        return converterParaDTO(turmaAtualizada);
    }
    
    public void deletarTurma(Long id) {
        Turma turma = turmaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Turma não encontrada com ID: " + id));
        turma.setAtiva(false);
        turmaRepository.save(turma);
    }
    
    private String gerarCodigoTurma() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    private TurmaDTO converterParaDTO(Turma turma) {
        return new TurmaDTO(
            turma.getId(),
            turma.getNome(),
            turma.getDescricao(),
            turma.getProfessor().getId(),
            turma.getCodigoTurma(),
            turma.getDataCriacao(),
            turma.isAtiva()
        );
    }
}
