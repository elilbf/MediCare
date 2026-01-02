package br.com.grupo.ClassInsight.controller;

import br.com.grupo.ClassInsight.dto.AvaliacaoRequestDTO;
import br.com.grupo.ClassInsight.dto.AvaliacaoResponseDTO;
import br.com.grupo.ClassInsight.model.Avaliacao;
import br.com.grupo.ClassInsight.model.Urgencia;
import br.com.grupo.ClassInsight.repository.AvaliacaoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Testes de Integração do AvaliacaoController")
class AvaliacaoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Avaliacao avaliacao;

    @BeforeEach
    void setUp() {
        avaliacaoRepository.deleteAll();

        avaliacao = Avaliacao.builder()
                .descricao("Aula excelente, professor muito didático!")
                .nota(9)
                .urgencia(Urgencia.POSITIVO)
                .dataEnvio(LocalDateTime.now())
                .notificacaoEnviada(false)
                .processado(false)
                .build();

        avaliacao = avaliacaoRepository.save(avaliacao);
    }

    @Test
    @DisplayName("Deve criar avaliação com sucesso")
    void criarAvaliacao_DeveRetornar201_QuandoRequestValido() throws Exception {
        AvaliacaoRequestDTO requestDTO = AvaliacaoRequestDTO.builder()
                .descricao("Ótima aula, aprendi muito!")
                .nota(8)
                .build();

        mockMvc.perform(post("/api/v1/avaliacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mensagem").value("Avaliação criada com sucesso"))
                .andExpect(jsonPath("$.descricao").value(requestDTO.getDescricao()))
                .andExpect(jsonPath("$.nota").value(requestDTO.getNota()))
                .andExpect(jsonPath("$.urgencia").value("MEDIO"))
                .andExpect(jsonPath("$.notificacaoEnviada").value(false))
                .andExpect(jsonPath("$.processado").value(false))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.dataEnvio").exists());
    }

    @Test
    @DisplayName("Deve retornar 400 quando descrição inválida")
    void criarAvaliacao_DeveRetornar400_QuandoDescricaoInvalida() throws Exception {
        AvaliacaoRequestDTO requestDTO = AvaliacaoRequestDTO.builder()
                .descricao("Curto")
                .nota(8)
                .build();

        mockMvc.perform(post("/api/v1/avaliacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Erro de Validação"))
                .andExpect(jsonPath("$.message").value("Dados inválidos fornecidos"));
    }

    @Test
    @DisplayName("Deve listar avaliações com paginação")
    void listarAvaliacoes_DeveRetornar200_QuandoChamado() throws Exception {
        mockMvc.perform(get("/api/v1/avaliacao")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id").value(avaliacao.getId().toString()))
                .andExpect(jsonPath("$.content[0].descricao").value(avaliacao.getDescricao()))
                .andExpect(jsonPath("$.content[0].nota").value(avaliacao.getNota()))
                .andExpect(jsonPath("$.content[0].urgencia").value(avaliacao.getUrgencia().toString()))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.number").value(0));
    }

    @Test
    @DisplayName("Deve buscar avaliação por ID com sucesso")
    void buscarAvaliacaoPorId_DeveRetornar200_QuandoIdExistente() throws Exception {
        mockMvc.perform(get("/api/v1/avaliacao/{id}", avaliacao.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(avaliacao.getId().toString()))
                .andExpect(jsonPath("$.descricao").value(avaliacao.getDescricao()))
                .andExpect(jsonPath("$.nota").value(avaliacao.getNota()))
                .andExpect(jsonPath("$.urgencia").value(avaliacao.getUrgencia().toString()))
                .andExpect(jsonPath("$.dataEnvio").exists());
    }

    @Test
    @DisplayName("Deve retornar 404 quando avaliação não encontrada")
    void buscarAvaliacaoPorId_DeveRetornar404_QuandoIdInexistente() throws Exception {
        UUID idInexistente = UUID.randomUUID();

        mockMvc.perform(get("/api/v1/avaliacao/{id}", idInexistente))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Entidade Não Encontrada"))
                .andExpect(jsonPath("$.message").value("Avaliação não encontrada com ID: " + idInexistente))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path").exists());
    }
}
