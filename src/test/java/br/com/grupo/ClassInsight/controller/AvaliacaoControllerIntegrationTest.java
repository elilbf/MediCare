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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

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
        // Arrange
        AvaliacaoRequestDTO requestDTO = AvaliacaoRequestDTO.builder()
                .descricao("Ótima aula, aprendi muito!")
                .nota(8)
                .build();

        // Act & Assert
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
        // Arrange
        AvaliacaoRequestDTO requestDTO = AvaliacaoRequestDTO.builder()
                .descricao("Curto")
                .nota(8)
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/v1/avaliacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Erro de Validação"))
                .andExpect(jsonPath("$.message").value("Dados inválidos fornecidos"))
                .andExpect(jsonPath("$.validationErrors.descricao").exists());
    }

    @Test
    @DisplayName("Deve retornar 400 quando nota inválida")
    void criarAvaliacao_DeveRetornar400_QuandoNotaInvalida() throws Exception {
        // Arrange
        AvaliacaoRequestDTO requestDTO = AvaliacaoRequestDTO.builder()
                .descricao("Descrição válida para teste de nota inválida")
                .nota(15)
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/v1/avaliacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Erro de Validação"))
                .andExpect(jsonPath("$.validationErrors.nota").exists());
    }

    @Test
    @DisplayName("Deve retornar 400 quando nota negativa")
    void criarAvaliacao_DeveRetornar400_QuandoNotaNegativa() throws Exception {
        // Arrange
        AvaliacaoRequestDTO requestDTO = AvaliacaoRequestDTO.builder()
                .descricao("Descrição válida para teste de nota negativa")
                .nota(-1)
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/v1/avaliacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Erro de Validação"))
                .andExpect(jsonPath("$.validationErrors.nota").exists());
    }

    @Test
    @DisplayName("Deve listar avaliações com paginação")
    void listarAvaliacoes_DeveRetornar200_QuandoChamado() throws Exception {
        // Act & Assert
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
    @DisplayName("Deve filtrar avaliações por urgência")
    void listarAvaliacoes_DeveFiltrarPorUrgencia_QuandoParametroFornecido() throws Exception {
        // Arrange - Criar avaliação crítica
        Avaliacao avaliacaoCritica = Avaliacao.builder()
                .descricao("Aula muito confusa")
                .nota(2)
                .urgencia(Urgencia.CRITICO)
                .dataEnvio(LocalDateTime.now())
                .notificacaoEnviada(false)
                .processado(false)
                .build();
        avaliacaoRepository.save(avaliacaoCritica);

        // Act & Assert
        mockMvc.perform(get("/api/v1/avaliacao")
                        .param("urgencia", "CRITICO")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].urgencia").value("CRITICO"))
                .andExpect(jsonPath("$.content[0].nota").value(2));
    }

    @Test
    @DisplayName("Deve filtrar avaliações por período")
    void listarAvaliacoes_DeveFiltrarPorPeriodo_QuandoDatasFornecidas() throws Exception {
        // Arrange
        LocalDateTime dataInicio = LocalDateTime.now().minusHours(1);
        LocalDateTime dataFim = LocalDateTime.now().plusHours(1);

        // Act & Assert
        mockMvc.perform(get("/api/v1/avaliacao")
                        .param("dataInicio", dataInicio.toString())
                        .param("dataFim", dataFim.toString())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id").value(avaliacao.getId().toString()));
    }

    @Test
    @DisplayName("Deve buscar avaliação por ID com sucesso")
    void buscarAvaliacaoPorId_DeveRetornar200_QuandoIdExistente() throws Exception {
        // Act & Assert
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
        // Arrange
        UUID idInexistente = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(get("/api/v1/avaliacao/{id}", idInexistente))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Entidade Não Encontrada"))
                .andExpect(jsonPath("$.message").value("Avaliação não encontrada com ID: " + idInexistente))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há avaliações")
    void listarAvaliacoes_DeveRetornarListaVazia_QuandoNaoHaAvaliacoes() throws Exception {
        // Arrange
        avaliacaoRepository.deleteAll();

        // Act & Assert
        mockMvc.perform(get("/api/v1/avaliacao")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.totalPages").value(0));
    }

    @Test
    @DisplayName("Deve respeitar ordenação por data de envio")
    void listarAvaliacoes_DeveOrdenarPorDataEnvioDesc_QuandoChamado() throws Exception {
        // Arrange - Criar avaliações com datas diferentes
        Avaliacao avaliacaoAntiga = Avaliacao.builder()
                .descricao("Aula antiga")
                .nota(7)
                .urgencia(Urgencia.POSITIVO)
                .dataEnvio(LocalDateTime.now().minusDays(2))
                .notificacaoEnviada(false)
                .processado(false)
                .build();
        avaliacaoAntiga = avaliacaoRepository.save(avaliacaoAntiga);

        Avaliacao avaliacaoRecente = Avaliacao.builder()
                .descricao("Aula recente")
                .nota(8)
                .urgencia(Urgencia.POSITIVO)
                .dataEnvio(LocalDateTime.now().minusMinutes(10))
                .notificacaoEnviada(false)
                .processado(false)
                .build();
        avaliacaoRecente = avaliacaoRepository.save(avaliacaoRecente);

        // Act & Assert
        mockMvc.perform(get("/api/v1/avaliacao")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.content[0].descricao").value(avaliacao.getDescricao())) // Mais recente
                .andExpect(jsonPath("$.content[1].descricao").value(avaliacaoRecente.getDescricao()))
                .andExpect(jsonPath("$.content[2].descricao").value(avaliacaoAntiga.getDescricao())); // Mais antiga
    }

    @Test
    @DisplayName("Deve lidar com paginação corretamente")
    void listarAvaliacoes_DeveFuncionarComPaginacao_QuandoParametrosValidos() throws Exception {
        // Arrange - Criar mais avaliações
        for (int i = 0; i < 5; i++) {
            Avaliacao novaAvaliacao = Avaliacao.builder()
                    .descricao("Avaliação " + i)
                    .nota(7 + i)
                    .urgencia(Urgencia.POSITIVO)
                    .dataEnvio(LocalDateTime.now().minusMinutes(i))
                    .notificacaoEnviada(false)
                    .processado(false)
                    .build();
            avaliacaoRepository.save(novaAvaliacao);
        }

        // Act & Assert - Primeira página
        mockMvc.perform(get("/api/v1/avaliacao")
                        .param("page", "0")
                        .param("size", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.totalElements").value(6))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.size").value(3))
                .andExpect(jsonPath("$.number").value(0));

        // Act & Assert - Segunda página
        mockMvc.perform(get("/api/v1/avaliacao")
                        .param("page", "1")
                        .param("size", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.totalElements").value(6))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.size").value(3))
                .andExpect(jsonPath("$.number").value(1));
    }
}
