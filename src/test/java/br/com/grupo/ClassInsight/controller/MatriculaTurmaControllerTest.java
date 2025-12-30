package br.com.grupo.ClassInsight.controller;

import br.com.grupo.ClassInsight.service.MatriculaTurmaService;
import br.com.grupo.ClassInsight.exception.ResourceNotFoundException;
import br.com.grupo.ClassInsight.exception.DuplicateResourceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MatriculaTurmaController.class)
@DisplayName("Testes do MatriculaTurmaController")
class MatriculaTurmaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MatriculaTurmaService matriculaTurmaService;

    @Test
    @DisplayName("Deve matricular aluno com sucesso")
    void testMatricularAlunoComSucesso() throws Exception {
        when(matriculaTurmaService.matricularAluno(1L, 1L)).thenReturn(new Object());

        mockMvc.perform(post("/classinsight/matriculas/aluno/1/turma/1"))
            .andExpect(status().isCreated());

        verify(matriculaTurmaService, times(1)).matricularAluno(1L, 1L);
    }

    @Test
    @DisplayName("Deve retornar erro ao tentar matricular em turma inativa")
    void testMatricularEmTurmaInativa() throws Exception {
        when(matriculaTurmaService.matricularAluno(1L, 1L))
            .thenThrow(new RuntimeException("Turma inativa"));

        mockMvc.perform(post("/classinsight/matriculas/aluno/1/turma/1"))
            .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("Deve retornar erro ao tentar matricular com aluno duplicado")
    void testMatricularAlunoJaMatriculado() throws Exception {
        when(matriculaTurmaService.matricularAluno(1L, 1L))
            .thenThrow(new DuplicateResourceException("Aluno já matriculado"));

        mockMvc.perform(post("/classinsight/matriculas/aluno/1/turma/1"))
            .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Deve desmatricular aluno com sucesso")
    void testDesmatricularAlunoComSucesso() throws Exception {
        doNothing().when(matriculaTurmaService).desmatricular(1L, 1L);

        mockMvc.perform(delete("/classinsight/matriculas/aluno/1/turma/1"))
            .andExpect(status().isNoContent());

        verify(matriculaTurmaService, times(1)).desmatricular(1L, 1L);
    }

    @Test
    @DisplayName("Deve retornar 404 ao desmatricular matrícula inexistente")
    void testDesmatricularMatriculaInexistente() throws Exception {
        doThrow(new ResourceNotFoundException("Matrícula não encontrada"))
            .when(matriculaTurmaService).desmatricular(999L, 999L);

        mockMvc.perform(delete("/classinsight/matriculas/aluno/999/turma/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve verificar matrícula com sucesso")
    void testVerificarMatriculaComSucesso() throws Exception {
        when(matriculaTurmaService.verificarMatricula(1L, 1L)).thenReturn(true);

        mockMvc.perform(get("/classinsight/matriculas/aluno/1/turma/1/verificar"))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve listar matrículas de um aluno")
    void testListarMatriculasPorAluno() throws Exception {
        mockMvc.perform(get("/classinsight/matriculas/aluno/1"))
            .andExpect(status().isOk());

        verify(matriculaTurmaService, times(1)).listarMatriculasPorAluno(1L);
    }

    @Test
    @DisplayName("Deve listar matrículas de uma turma")
    void testListarMatriculasPorTurma() throws Exception {
        mockMvc.perform(get("/classinsight/matriculas/turma/1"))
            .andExpect(status().isOk());

        verify(matriculaTurmaService, times(1)).listarMatriculasPorTurma(1L);
    }
}
