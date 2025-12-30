package br.com.grupo.ClassInsight.service;

import br.com.grupo.ClassInsight.model.Feedback;
import br.com.grupo.ClassInsight.model.Turma;
import br.com.grupo.ClassInsight.model.Usuario;
import br.com.grupo.ClassInsight.model.TipoUsuario;
import br.com.grupo.ClassInsight.model.TipoFeedback;
import br.com.grupo.ClassInsight.model.StatusFeedback;
import br.com.grupo.ClassInsight.dto.FeedbackCriacaoDTO;
import br.com.grupo.ClassInsight.dto.FeedbackDTO;
import br.com.grupo.ClassInsight.dto.RespostaFeedbackDTO;
import br.com.grupo.ClassInsight.repository.FeedbackRepository;
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
@DisplayName("Testes do FeedbackService")
class FeedbackServiceTest {

    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private TurmaRepository turmaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private FeedbackService feedbackService;

    private Feedback feedback;
    private Usuario aluno;
    private Usuario professor;
    private Turma turma;
    private FeedbackCriacaoDTO feedbackCriacaoDTO;

    @BeforeEach
    void setUp() {
        aluno = new Usuario();
        aluno.setId(1L);
        aluno.setEmail("aluno@example.com");
        aluno.setNome("João Aluno");
        aluno.setTipoUsuario(TipoUsuario.ALUNO);
        aluno.setAtivo(true);

        professor = new Usuario();
        professor.setId(2L);
        professor.setEmail("professor@example.com");
        professor.setNome("Prof. Maria");
        professor.setTipoUsuario(TipoUsuario.PROFESSOR);
        professor.setAtivo(true);

        turma = new Turma();
        turma.setId(1L);
        turma.setNome("Matemática");
        turma.setDescricao("Aula de cálculo");
        turma.setProfessor(professor);
        turma.setAtiva(true);

        feedback = new Feedback();
        feedback.setId(1L);
        feedback.setTurma(turma);
        feedback.setAluno(aluno);
        feedback.setTitulo("Dúvida sobre integral");
        feedback.setConteudo("Não entendi como resolver a integral definida");
        feedback.setTipoFeedback(TipoFeedback.DUVIDA);
        feedback.setStatus(StatusFeedback.ABERTO);
        feedback.setDataCriacao(LocalDateTime.now());

        feedbackCriacaoDTO = new FeedbackCriacaoDTO(
            1L,
            "Dúvida sobre integral",
            "Não entendi como resolver a integral definida",
            TipoFeedback.DUVIDA
        );
    }

    @Test
    @DisplayName("Deve criar um feedback com sucesso")
    void testCriarFeedbackComSucesso() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(turmaRepository.findById(1L)).thenReturn(Optional.of(turma));
        when(feedbackRepository.save(any(Feedback.class))).thenReturn(feedback);

        FeedbackDTO resultado = feedbackService.criarFeedback(1L, feedbackCriacaoDTO);

        assertNotNull(resultado);
        assertEquals("Dúvida sobre integral", resultado.titulo());
        assertEquals(StatusFeedback.ABERTO, resultado.status());
        verify(feedbackRepository, times(1)).save(any(Feedback.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar feedback com aluno inexistente")
    void testCriarFeedbackComAlunoInexistente() {
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            feedbackService.criarFeedback(999L, feedbackCriacaoDTO);
        });
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar feedback com turma inexistente")
    void testCriarFeedbackComTurmaInexistente() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(turmaRepository.findById(999L)).thenReturn(Optional.empty());

        FeedbackCriacaoDTO dto = new FeedbackCriacaoDTO(999L, "Title", "Content", TipoFeedback.DUVIDA);

        assertThrows(ResourceNotFoundException.class, () -> {
            feedbackService.criarFeedback(1L, dto);
        });
    }

    @Test
    @DisplayName("Deve obter feedback por ID com sucesso")
    void testObterFeedbackPorIdComSucesso() {
        when(feedbackRepository.findById(1L)).thenReturn(Optional.of(feedback));

        FeedbackDTO resultado = feedbackService.obterFeedbackPorId(1L);

        assertNotNull(resultado);
        assertEquals("Dúvida sobre integral", resultado.titulo());
    }

    @Test
    @DisplayName("Deve lançar exceção ao obter feedback inexistente")
    void testObterFeedbackInexistente() {
        when(feedbackRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            feedbackService.obterFeedbackPorId(999L);
        });
    }

    @Test
    @DisplayName("Deve listar feedbacks por turma")
    void testListarFeedbackPorTurma() {
        List<Feedback> feedbacks = List.of(feedback);
        when(turmaRepository.findById(1L)).thenReturn(Optional.of(turma));
        when(feedbackRepository.findByTurma(turma)).thenReturn(feedbacks);

        List<FeedbackDTO> resultado = feedbackService.listarFeedbackPorTurma(1L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("Deve listar feedbacks por aluno")
    void testListarFeedbackPorAluno() {
        List<Feedback> feedbacks = List.of(feedback);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(feedbackRepository.findByAluno(aluno)).thenReturn(feedbacks);

        List<FeedbackDTO> resultado = feedbackService.listarFeedbackPorAluno(1L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("Deve listar feedbacks por status")
    void testListarFeedbackPorStatus() {
        List<Feedback> feedbacks = List.of(feedback);
        when(feedbackRepository.findByStatus(StatusFeedback.ABERTO)).thenReturn(feedbacks);

        List<FeedbackDTO> resultado = feedbackService.listarFeedbackPorStatus(StatusFeedback.ABERTO);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("Deve responder um feedback com sucesso")
    void testResponderFeedbackComSucesso() {
        feedback.setStatus(StatusFeedback.ABERTO);
        when(feedbackRepository.findById(1L)).thenReturn(Optional.of(feedback));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(professor));
        when(feedbackRepository.save(any(Feedback.class))).thenReturn(feedback);

        RespostaFeedbackDTO respostaDTO = new RespostaFeedbackDTO("Para resolver integrais...");

        FeedbackDTO resultado = feedbackService.responderFeedback(1L, 2L, respostaDTO);

        assertNotNull(resultado);
        assertEquals(StatusFeedback.RESPONDIDO, resultado.status());
        assertNotNull(resultado.resposta());
    }

    @Test
    @DisplayName("Deve lançar exceção ao responder feedback fechado")
    void testResponderFeedbackFechado() {
        feedback.setStatus(StatusFeedback.FECHADO);
        when(feedbackRepository.findById(1L)).thenReturn(Optional.of(feedback));

        RespostaFeedbackDTO respostaDTO = new RespostaFeedbackDTO("Resposta");

        assertThrows(InvalidOperationException.class, () -> {
            feedbackService.responderFeedback(1L, 2L, respostaDTO);
        });
    }

    @Test
    @DisplayName("Deve fechar um feedback com sucesso")
    void testFecharFeedbackComSucesso() {
        feedback.setStatus(StatusFeedback.RESPONDIDO);
        when(feedbackRepository.findById(1L)).thenReturn(Optional.of(feedback));
        when(feedbackRepository.save(any(Feedback.class))).thenReturn(feedback);

        FeedbackDTO resultado = feedbackService.fecharFeedback(1L);

        assertNotNull(resultado);
        assertEquals(StatusFeedback.FECHADO, resultado.status());
    }

    @Test
    @DisplayName("Deve listar feedbacks abertos")
    void testListarFeedbackAberto() {
        List<Feedback> feedbacks = List.of(feedback);
        when(feedbackRepository.findByStatus(StatusFeedback.ABERTO)).thenReturn(feedbacks);

        List<FeedbackDTO> resultado = feedbackService.listarFeedbackAberto();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("Deve deletar um feedback com sucesso")
    void testDeletarFeedbackComSucesso() {
        when(feedbackRepository.findById(1L)).thenReturn(Optional.of(feedback));

        feedbackService.deletarFeedback(1L);

        verify(feedbackRepository, times(1)).deleteById(1L);
    }
}
