package br.com.grupo.ClassInsight.controller;

import br.com.grupo.ClassInsight.dto.TurmaCriacaoDTO;
import br.com.grupo.ClassInsight.dto.TurmaDTO;
import br.com.grupo.ClassInsight.service.TurmaService;
import br.com.grupo.ClassInsight.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
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

@WebMvcTest(TurmaController.class)
@DisplayName("Testes do TurmaController")
class TurmaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TurmaService turmaService;

    @Autowired
    private ObjectMapper objectMapper;

    private TurmaCriacaoDTO turmaCriacaoDTO;
    private TurmaDTO turmaDTO;

    @BeforeEach
    void setUp() {
        turmaCriacaoDTO = new TurmaCriacaoDTO(
            "Matemática",
            "Aula de cálculo",
            1L
        );

        turmaDTO = new TurmaDTO(
            1L,
            "Matemática",
            "Aula de cálculo",
            1L,
            "A1B2C3D4",
            LocalDateTime.now(),
            true
        );
    }

    @Test
    @DisplayName("Deve criar turma com sucesso")
    void testCriarTurmaComSucesso() throws Exception {
        when(turmaService.criarTurma(any(TurmaCriacaoDTO.class))).thenReturn(turmaDTO);

        mockMvc.perform(post("/classinsight/turmas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(turmaCriacaoDTO)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.nome").value("Matemática"));

        verify(turmaService, times(1)).criarTurma(any(TurmaCriacaoDTO.class));
    }

    @Test
    @DisplayName("Deve obter turma por ID com sucesso")
    void testObterTurmaPorIdComSucesso() throws Exception {
        when(turmaService.obterTurmaPorId(1L)).thenReturn(turmaDTO);

        mockMvc.perform(get("/classinsight/turmas/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.nome").value("Matemática"));
    }

    @Test
    @DisplayName("Deve retornar 404 ao obter turma inexistente")
    void testObterTurmaPorIdNaoEncontrada() throws Exception {
        when(turmaService.obterTurmaPorId(999L))
            .thenThrow(new ResourceNotFoundException("Turma não encontrada"));

        mockMvc.perform(get("/classinsight/turmas/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve obter turma por código com sucesso")
    void testObterTurmaPorCodigoComSucesso() throws Exception {
        when(turmaService.obterTurmaPorCodigo("A1B2C3D4")).thenReturn(turmaDTO);

        mockMvc.perform(get("/classinsight/turmas/codigo/A1B2C3D4"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.codigoTurma").value("A1B2C3D4"));
    }

    @Test
    @DisplayName("Deve listar todas as turmas")
    void testListarTodasTurmas() throws Exception {
        List<TurmaDTO> turmas = List.of(turmaDTO);
        when(turmaService.listarTodas()).thenReturn(turmas);

        mockMvc.perform(get("/classinsight/turmas"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1L))
            .andExpect(jsonPath("$[0].nome").value("Matemática"));
    }

    @Test
    @DisplayName("Deve listar turmas de um professor")
    void testListarTurmasPorProfessor() throws Exception {
        List<TurmaDTO> turmas = List.of(turmaDTO);
        when(turmaService.listarPorProfessor(1L)).thenReturn(turmas);

        mockMvc.perform(get("/classinsight/turmas/professor/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].professorId").value(1L));
    }

    @Test
    @DisplayName("Deve atualizar turma com sucesso")
    void testAtualizarTurmaComSucesso() throws Exception {
        when(turmaService.atualizarTurma(eq(1L), any(TurmaCriacaoDTO.class)))
            .thenReturn(turmaDTO);

        mockMvc.perform(put("/classinsight/turmas/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(turmaCriacaoDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("Deve deletar turma com sucesso")
    void testDeletarTurmaComSucesso() throws Exception {
        doNothing().when(turmaService).deletarTurma(1L);

        mockMvc.perform(delete("/classinsight/turmas/1"))
            .andExpect(status().isNoContent());

        verify(turmaService, times(1)).deletarTurma(1L);
    }
}
