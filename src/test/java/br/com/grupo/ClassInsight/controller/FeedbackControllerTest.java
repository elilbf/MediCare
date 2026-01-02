package br.com.grupo.ClassInsight.controller;

import br.com.grupo.ClassInsight.model.TipoFeedback;
import br.com.grupo.ClassInsight.model.StatusFeedback;
import br.com.grupo.ClassInsight.dto.FeedbackCriacaoDTO;
import br.com.grupo.ClassInsight.dto.FeedbackDTO;
import br.com.grupo.ClassInsight.dto.RespostaFeedbackDTO;
import br.com.grupo.ClassInsight.service.FeedbackService;
import br.com.grupo.ClassInsight.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FeedbackController.class)
@DisplayName("Testes do FeedbackController")
class FeedbackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FeedbackService feedbackService;

    @Autowired
    private ObjectMapper objectMapper;

    private FeedbackCriacaoDTO feedbackCriacaoDTO;
    private FeedbackDTO feedbackDTO;

    @BeforeEach
    void setUp() {
        feedbackCriacaoDTO = new FeedbackCriacaoDTO(
            1L,
            "Dúvida sobre integral",
            "Não entendi como resolver a integral definida",
            TipoFeedback.DUVIDA
        );

        feedbackDTO = new FeedbackDTO(
            1L,
            1L,
            1L,
            "Dúvida sobre integral",
            "Não entendi como resolver a integral definida",
            TipoFeedback.DUVIDA,
            StatusFeedback.ABERTO,
            null,
            LocalDateTime.now(),
            null
        );
    }

    @Test
    @DisplayName("Deve criar feedback com sucesso")
    void testCriarFeedbackComSucesso() throws Exception {
        when(feedbackService.criarFeedback(eq(1L), any(FeedbackCriacaoDTO.class)))
            .thenReturn(feedbackDTO);

        mockMvc.perform(post("/api/feedbacks/aluno/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(feedbackCriacaoDTO)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.titulo").value("Dúvida sobre integral"));

        verify(feedbackService, times(1)).criarFeedback(eq(1L), any(FeedbackCriacaoDTO.class));
    }

    @Test
    @DisplayName("Deve obter feedback por ID com sucesso")
    void testObterFeedbackPorIdComSucesso() throws Exception {
        when(feedbackService.obterFeedbackPorId(1L)).thenReturn(feedbackDTO);

        mockMvc.perform(get("/api/feedbacks/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.titulo").value("Dúvida sobre integral"));
    }

    @Test
    @DisplayName("Deve retornar 404 ao obter feedback inexistente")
    void testObterFeedbackPorIdNaoEncontrado() throws Exception {
        when(feedbackService.obterFeedbackPorId(999L))
            .thenThrow(new ResourceNotFoundException("Feedback não encontrado"));

        mockMvc.perform(get("/api/feedbacks/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve listar feedbacks de uma turma")
    void testListarFeedbackPorTurma() throws Exception {
        List<FeedbackDTO> feedbacks = List.of(feedbackDTO);
        when(feedbackService.listarFeedbackPorTurma(1L)).thenReturn(feedbacks);

        mockMvc.perform(get("/api/feedbacks/turma/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @DisplayName("Deve listar feedbacks de um aluno")
    void testListarFeedbackPorAluno() throws Exception {
        List<FeedbackDTO> feedbacks = List.of(feedbackDTO);
        when(feedbackService.listarFeedbackPorAluno(1L)).thenReturn(feedbacks);

        mockMvc.perform(get("/api/feedbacks/aluno/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @DisplayName("Deve listar feedbacks por status")
    void testListarFeedbackPorStatus() throws Exception {
        List<FeedbackDTO> feedbacks = List.of(feedbackDTO);
        when(feedbackService.listarFeedbackPorStatus(StatusFeedback.ABERTO))
            .thenReturn(feedbacks);

        mockMvc.perform(get("/api/feedbacks/status/ABERTO"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @DisplayName("Deve listar feedbacks abertos")
    void testListarFeedbackAberto() throws Exception {
        List<FeedbackDTO> feedbacks = List.of(feedbackDTO);
        when(feedbackService.listarFeedbackAberto()).thenReturn(feedbacks);

        mockMvc.perform(get("/api/feedbacks/status/aberto"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].status").value("ABERTO"));
    }

    @Test
    @DisplayName("Deve responder um feedback com sucesso")
    void testResponderFeedbackComSucesso() throws Exception {
        RespostaFeedbackDTO respostaDTO = new RespostaFeedbackDTO("Para resolver integrais...");
        FeedbackDTO feedbackRespondido = new FeedbackDTO(
            1L, 1L, 1L, "Dúvida sobre integral", "Conteúdo",
            TipoFeedback.DUVIDA, StatusFeedback.RESPONDIDO, "Para resolver integrais...", LocalDateTime.now(), null
        );

        when(feedbackService.responderFeedback(eq(1L), eq(2L), any(RespostaFeedbackDTO.class)))
            .thenReturn(feedbackRespondido);

        mockMvc.perform(post("/api/feedbacks/1/responder/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(respostaDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("RESPONDIDO"));

        verify(feedbackService, times(1)).responderFeedback(eq(1L), eq(2L), any(RespostaFeedbackDTO.class));
    }

    @Test
    @DisplayName("Deve fechar um feedback com sucesso")
    void testFecharFeedbackComSucesso() throws Exception {
        FeedbackDTO feedbackFechado = new FeedbackDTO(
            1L, 1L, 1L, "Dúvida sobre integral", "Conteúdo",
            TipoFeedback.DUVIDA, StatusFeedback.FECHADO, "Resposta", LocalDateTime.now(), null
        );

        when(feedbackService.fecharFeedback(1L)).thenReturn(feedbackFechado);

        mockMvc.perform(put("/api/feedbacks/1/fechar"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("FECHADO"));
    }

    @Test
    @DisplayName("Deve deletar feedback com sucesso")
    void testDeletarFeedbackComSucesso() throws Exception {
        doNothing().when(feedbackService).deletarFeedback(1L);

        mockMvc.perform(delete("/api/feedbacks/1"))
            .andExpect(status().isNoContent());

        verify(feedbackService, times(1)).deletarFeedback(1L);
    }
}
