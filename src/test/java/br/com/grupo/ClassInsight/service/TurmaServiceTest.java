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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do TurmaService")
class TurmaServiceTest {

    @Mock
    private TurmaRepository turmaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private TurmaService turmaService;

    private Turma turma;
    private Usuario professor;
    private TurmaCriacaoDTO turmaCriacaoDTO;

    @BeforeEach
    void setUp() {
        professor = new Usuario();
        professor.setId(1L);
        professor.setEmail("professor@example.com");
        professor.setNome("Prof. Maria");
        professor.setTipoUsuario(TipoUsuario.PROFESSOR);
        professor.setAtivo(true);

        turma = new Turma();
        turma.setId(1L);
        turma.setNome("Matemática");
        turma.setDescricao("Aula de cálculo para o 3º ano");
        turma.setProfessor(professor);
        turma.setCodigoTurma("A1B2C3D4");
        turma.setAtiva(true);
        turma.setDataCriacao(LocalDateTime.now());

        turmaCriacaoDTO = new TurmaCriacaoDTO(
            "Matemática",
            "Aula de cálculo para o 3º ano",
            1L
        );
    }

    @Test
    @DisplayName("Deve criar uma turma com sucesso")
    void testCriarTurmaComSucesso() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(professor));
        when(turmaRepository.save(any(Turma.class))).thenReturn(turma);

        TurmaDTO resultado = turmaService.criarTurma(turmaCriacaoDTO);

        assertNotNull(resultado);
        assertEquals("Matemática", resultado.nome());
        verify(turmaRepository, times(1)).save(any(Turma.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar turma com professor inválido")
    void testCriarTurmaComProfessorInvalido() {
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        TurmaCriacaoDTO dto = new TurmaCriacaoDTO("Math", "Description", 999L);

        assertThrows(ResourceNotFoundException.class, () -> {
            turmaService.criarTurma(dto);
        });
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar turma com usuário que não é professor")
    void testCriarTurmaComUsuarioQuenaoeProfessor() {
        Usuario aluno = new Usuario();
        aluno.setId(2L);
        aluno.setTipoUsuario(TipoUsuario.ALUNO);
        
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(aluno));

        TurmaCriacaoDTO dto = new TurmaCriacaoDTO("Math", "Description", 2L);

        assertThrows(InvalidOperationException.class, () -> {
            turmaService.criarTurma(dto);
        });
    }

    @Test
    @DisplayName("Deve obter turma por ID com sucesso")
    void testObterTurmaPorIdComSucesso() {
        when(turmaRepository.findById(1L)).thenReturn(Optional.of(turma));

        TurmaDTO resultado = turmaService.obterTurmaPorId(1L);

        assertNotNull(resultado);
        assertEquals("Matemática", resultado.nome());
    }

    @Test
    @DisplayName("Deve lançar exceção ao obter turma que não existe")
    void testObterTurmaPorIdNaoExiste() {
        when(turmaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            turmaService.obterTurmaPorId(999L);
        });
    }

    @Test
    @DisplayName("Deve obter turma por código com sucesso")
    void testObterTurmaPorCodigoComSucesso() {
        when(turmaRepository.findByCodigoTurma("A1B2C3D4")).thenReturn(Optional.of(turma));

        TurmaDTO resultado = turmaService.obterTurmaPorCodigo("A1B2C3D4");

        assertNotNull(resultado);
        assertEquals("A1B2C3D4", resultado.codigoTurma());
    }

    @Test
    @DisplayName("Deve listar todas as turmas ativas")
    void testListarTodas() {
        List<Turma> turmas = List.of(turma);
        when(turmaRepository.findByAtivaTrue()).thenReturn(turmas);

        List<TurmaDTO> resultado = turmaService.listarTodas();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("Deve listar turmas de um professor")
    void testListarPorProfessor() {
        List<Turma> turmas = List.of(turma);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(professor));
        when(turmaRepository.findByProfessor(professor)).thenReturn(turmas);

        List<TurmaDTO> resultado = turmaService.listarPorProfessor(1L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("Deve atualizar turma com sucesso")
    void testAtualizarTurmaComSucesso() {
        when(turmaRepository.findById(1L)).thenReturn(Optional.of(turma));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(professor));
        when(turmaRepository.save(any(Turma.class))).thenReturn(turma);

        TurmaDTO resultado = turmaService.atualizarTurma(1L, turmaCriacaoDTO);

        assertNotNull(resultado);
        verify(turmaRepository, times(1)).save(any(Turma.class));
    }

    @Test
    @DisplayName("Deve deletar turma com sucesso")
    void testDeletarTurmaComSucesso() {
        when(turmaRepository.findById(1L)).thenReturn(Optional.of(turma));
        when(turmaRepository.save(any(Turma.class))).thenReturn(turma);

        turmaService.deletarTurma(1L);

        verify(turmaRepository, times(1)).save(any(Turma.class));
    }
}
