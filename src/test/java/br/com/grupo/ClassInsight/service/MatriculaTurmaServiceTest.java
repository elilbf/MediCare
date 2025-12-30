package br.com.grupo.ClassInsight.service;

import br.com.grupo.ClassInsight.model.MatriculaTurma;
import br.com.grupo.ClassInsight.model.Turma;
import br.com.grupo.ClassInsight.model.Usuario;
import br.com.grupo.ClassInsight.model.TipoUsuario;
import br.com.grupo.ClassInsight.repository.MatriculaTurmaRepository;
import br.com.grupo.ClassInsight.repository.TurmaRepository;
import br.com.grupo.ClassInsight.repository.UsuarioRepository;
import br.com.grupo.ClassInsight.exception.ResourceNotFoundException;
import br.com.grupo.ClassInsight.exception.DuplicateResourceException;
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
@DisplayName("Testes do MatriculaTurmaService")
class MatriculaTurmaServiceTest {

    @Mock
    private MatriculaTurmaRepository matriculaTurmaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private TurmaRepository turmaRepository;

    @InjectMocks
    private MatriculaTurmaService matriculaTurmaService;

    private MatriculaTurma matricula;
    private Usuario aluno;
    private Turma turma;

    @BeforeEach
    void setUp() {
        aluno = new Usuario();
        aluno.setId(1L);
        aluno.setEmail("aluno@example.com");
        aluno.setNome("João Aluno");
        aluno.setTipoUsuario(TipoUsuario.ALUNO);
        aluno.setAtivo(true);

        turma = new Turma();
        turma.setId(1L);
        turma.setNome("Matemática");
        turma.setDescricao("Aula de cálculo");
        turma.setAtiva(true);

        matricula = new MatriculaTurma();
        matricula.setId(1L);
        matricula.setAluno(aluno);
        matricula.setTurma(turma);
        matricula.setAtiva(true);
        matricula.setDataMatricula(LocalDateTime.now());
    }

    @Test
    @DisplayName("Deve matricular um aluno com sucesso")
    void testMatricularAlunoComSucesso() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(turmaRepository.findById(1L)).thenReturn(Optional.of(turma));
        when(matriculaTurmaRepository.findByAlunoAndTurma(aluno, turma)).thenReturn(Optional.empty());
        when(matriculaTurmaRepository.save(any(MatriculaTurma.class))).thenReturn(matricula);

        MatriculaTurma resultado = matriculaTurmaService.matricularAluno(1L, 1L);

        assertNotNull(resultado);
        assertTrue(resultado.isAtiva());
        verify(matriculaTurmaRepository, times(1)).save(any(MatriculaTurma.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao matricular com aluno inexistente")
    void testMatricularAlunoInexistente() {
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            matriculaTurmaService.matricularAluno(999L, 1L);
        });
    }

    @Test
    @DisplayName("Deve lançar exceção ao matricular com turma inexistente")
    void testMatricularTurmaInexistente() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(turmaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            matriculaTurmaService.matricularAluno(1L, 999L);
        });
    }

    @Test
    @DisplayName("Deve lançar exceção ao matricular em turma inativa")
    void testMatricularEmTurmaInativa() {
        turma.setAtiva(false);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(turmaRepository.findById(1L)).thenReturn(Optional.of(turma));

        assertThrows(InvalidOperationException.class, () -> {
            matriculaTurmaService.matricularAluno(1L, 1L);
        });
    }

    @Test
    @DisplayName("Deve lançar exceção ao matricular aluno que já está matriculado")
    void testMatricularAlunoJaMatriculado() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(turmaRepository.findById(1L)).thenReturn(Optional.of(turma));
        when(matriculaTurmaRepository.findByAlunoAndTurma(aluno, turma)).thenReturn(Optional.of(matricula));

        assertThrows(DuplicateResourceException.class, () -> {
            matriculaTurmaService.matricularAluno(1L, 1L);
        });
    }

    @Test
    @DisplayName("Deve desmatricular um aluno com sucesso")
    void testDesmatricularComSucesso() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(turmaRepository.findById(1L)).thenReturn(Optional.of(turma));
        when(matriculaTurmaRepository.findByAlunoAndTurma(aluno, turma)).thenReturn(Optional.of(matricula));
        when(matriculaTurmaRepository.save(any(MatriculaTurma.class))).thenReturn(matricula);

        matriculaTurmaService.desmatricular(1L, 1L);

        verify(matriculaTurmaRepository, times(1)).save(any(MatriculaTurma.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao desmatricular matrícula inexistente")
    void testDesmatricularMatriculaInexistente() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(turmaRepository.findById(1L)).thenReturn(Optional.of(turma));
        when(matriculaTurmaRepository.findByAlunoAndTurma(aluno, turma)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            matriculaTurmaService.desmatricular(1L, 1L);
        });
    }

    @Test
    @DisplayName("Deve listar matrículas de um aluno")
    void testListarMatriculasPorAluno() {
        List<MatriculaTurma> matriculas = List.of(matricula);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(matriculaTurmaRepository.findByAlunoAndAtivaTrue(aluno)).thenReturn(matriculas);

        List<MatriculaTurma> resultado = matriculaTurmaService.listarMatriculasPorAluno(1L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("Deve listar matrículas de uma turma")
    void testListarMatriculasPorTurma() {
        List<MatriculaTurma> matriculas = List.of(matricula);
        when(turmaRepository.findById(1L)).thenReturn(Optional.of(turma));
        when(matriculaTurmaRepository.findByTurmaAndAtivaTrue(turma)).thenReturn(matriculas);

        List<MatriculaTurma> resultado = matriculaTurmaService.listarMatriculasPorTurma(1L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("Deve verificar matrícula ativa")
    void testVerificarMatriculaAtiva() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(turmaRepository.findById(1L)).thenReturn(Optional.of(turma));
        when(matriculaTurmaRepository.findByAlunoAndTurma(aluno, turma)).thenReturn(Optional.of(matricula));

        boolean resultado = matriculaTurmaService.verificarMatricula(1L, 1L);

        assertTrue(resultado);
    }

    @Test
    @DisplayName("Deve retornar false para matrícula inativa")
    void testVerificarMatriculaInativa() {
        matricula.setAtiva(false);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(turmaRepository.findById(1L)).thenReturn(Optional.of(turma));
        when(matriculaTurmaRepository.findByAlunoAndTurma(aluno, turma)).thenReturn(Optional.of(matricula));

        boolean resultado = matriculaTurmaService.verificarMatricula(1L, 1L);

        assertFalse(resultado);
    }
}
