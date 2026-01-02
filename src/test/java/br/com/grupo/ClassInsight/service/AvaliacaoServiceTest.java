package br.com.grupo.ClassInsight.service;

import br.com.grupo.ClassInsight.dto.AvaliacaoRequestDTO;
import br.com.grupo.ClassInsight.dto.AvaliacaoResponseDTO;
import br.com.grupo.ClassInsight.exception.EntityNotFoundException;
import br.com.grupo.ClassInsight.model.Avaliacao;
import br.com.grupo.ClassInsight.model.Urgencia;
import br.com.grupo.ClassInsight.repository.AvaliacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do AvaliacaoService")
class AvaliacaoServiceTest {

    @Mock
    private AvaliacaoRepository avaliacaoRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private AvaliacaoService avaliacaoService;

    private AvaliacaoRequestDTO avaliacaoRequestDTO;
    private Avaliacao avaliacao;
    private UUID avaliacaoId;

    @BeforeEach
    void setUp() {
        avaliacaoId = UUID.randomUUID();
        avaliacaoRequestDTO = AvaliacaoRequestDTO.builder()
                .descricao("Aula excelente, professor muito didático!")
                .nota(9)
                .build();

        avaliacao = Avaliacao.builder()
                .id(avaliacaoId)
                .descricao(avaliacaoRequestDTO.getDescricao())
                .nota(avaliacaoRequestDTO.getNota())
                .urgencia(Urgencia.POSITIVO)
                .dataEnvio(LocalDateTime.now())
                .notificacaoEnviada(false)
                .processado(false)
                .build();
    }

    @Test
    @DisplayName("Deve criar avaliação com sucesso")
    void criarAvaliacao_DeveRetornarAvaliacaoResponseDTO_QuandoRequestValido() {
        // Arrange
        when(avaliacaoRepository.save(any(Avaliacao.class))).thenReturn(avaliacao);

        // Act
        AvaliacaoResponseDTO resultado = avaliacaoService.criarAvaliacao(avaliacaoRequestDTO);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(avaliacaoId);
        assertThat(resultado.getDescricao()).isEqualTo(avaliacaoRequestDTO.getDescricao());
        assertThat(resultado.getNota()).isEqualTo(avaliacaoRequestDTO.getNota());
        assertThat(resultado.getUrgencia()).isEqualTo(Urgencia.POSITIVO);
        assertThat(resultado.getMensagem()).isEqualTo("Avaliação criada com sucesso");
        assertThat(resultado.getNotificacaoEnviada()).isFalse();
        assertThat(resultado.getProcessado()).isFalse();

        verify(avaliacaoRepository).save(any(Avaliacao.class));
        verify(notificationService, never()).enviarNotificacaoCriticaAsync(any());
    }

    @Test
    @DisplayName("Deve criar avaliação crítica e enviar notificação")
    void criarAvaliacao_DeveEnviarNotificacao_QuandoAvaliacaoCritica() {
        // Arrange
        avaliacaoRequestDTO.setNota(2);
        avaliacao.setNota(2);
        avaliacao.setUrgencia(Urgencia.CRITICO);

        when(avaliacaoRepository.save(any(Avaliacao.class))).thenReturn(avaliacao);

        // Act
        AvaliacaoResponseDTO resultado = avaliacaoService.criarAvaliacao(avaliacaoRequestDTO);

        // Assert
        assertThat(resultado.getUrgencia()).isEqualTo(Urgencia.CRITICO);
        verify(avaliacaoRepository).save(any(Avaliacao.class));
        verify(notificationService).enviarNotificacaoCriticaAsync(any(Avaliacao.class));
    }

    @Test
    @DisplayName("Deve criar avaliação média sem notificação")
    void criarAvaliacao_NaoDeveEnviarNotificacao_QuandoAvaliacaoMedia() {
        // Arrange
        avaliacaoRequestDTO.setNota(6);
        avaliacao.setNota(6);
        avaliacao.setUrgencia(Urgencia.MEDIO);

        when(avaliacaoRepository.save(any(Avaliacao.class))).thenReturn(avaliacao);

        // Act
        AvaliacaoResponseDTO resultado = avaliacaoService.criarAvaliacao(avaliacaoRequestDTO);

        // Assert
        assertThat(resultado.getUrgencia()).isEqualTo(Urgencia.MEDIO);
        verify(avaliacaoRepository).save(any(Avaliacao.class));
        verify(notificationService, never()).enviarNotificacaoCriticaAsync(any());
    }

    @Test
    @DisplayName("Deve listar avaliações com paginação")
    void listarAvaliacoes_DeveRetornarPaginaDeAvaliacoes_QuandoChamado() {
        // Arrange
        List<Avaliacao> avaliacoes = Arrays.asList(avaliacao);
        Page<Avaliacao> paginaAvaliacoes = new PageImpl<>(avaliacoes);
        
        when(avaliacaoRepository.findAll(any(Pageable.class))).thenReturn(paginaAvaliacoes);

        // Act
        Page<AvaliacaoResponseDTO> resultado = avaliacaoService.listarAvaliacoes(
                org.springframework.data.domain.PageRequest.of(0, 10), 
                null, null, null);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).getId()).isEqualTo(avaliacaoId);
        verify(avaliacaoRepository).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Deve filtrar avaliações por urgência")
    void listarAvaliacoes_DeveFiltrarPorUrgencia_QuandoParametroFornecido() {
        // Arrange
        List<Avaliacao> avaliacoes = Arrays.asList(avaliacao);
        Page<Avaliacao> paginaAvaliacoes = new PageImpl<>(avaliacoes);
        
        when(avaliacaoRepository.findByUrgencia(eq(Urgencia.CRITICO), any(Pageable.class)))
                .thenReturn(paginaAvaliacoes);

        // Act
        Page<AvaliacaoResponseDTO> resultado = avaliacaoService.listarAvaliacoes(
                org.springframework.data.domain.PageRequest.of(0, 10), 
                Urgencia.CRITICO, null, null);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
        verify(avaliacaoRepository).findByUrgencia(eq(Urgencia.CRITICO), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve filtrar avaliações por período")
    void listarAvaliacoes_DeveFiltrarPorPeriodo_QuandoDatasFornecidas() {
        // Arrange
        LocalDateTime dataInicio = LocalDateTime.now().minusDays(7);
        LocalDateTime dataFim = LocalDateTime.now();
        List<Avaliacao> avaliacoes = Arrays.asList(avaliacao);
        Page<Avaliacao> paginaAvaliacoes = new PageImpl<>(avaliacoes);
        
        when(avaliacaoRepository.findByDataEnvioBetween(eq(dataInicio), eq(dataFim), any(Pageable.class)))
                .thenReturn(paginaAvaliacoes);

        // Act
        Page<AvaliacaoResponseDTO> resultado = avaliacaoService.listarAvaliacoes(
                org.springframework.data.domain.PageRequest.of(0, 10), 
                null, dataInicio, dataFim);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
        verify(avaliacaoRepository).findByDataEnvioBetween(eq(dataInicio), eq(dataFim), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve buscar avaliação por ID com sucesso")
    void buscarAvaliacaoPorId_DeveRetornarAvaliacao_QuandoIdExistente() {
        // Arrange
        when(avaliacaoRepository.findById(avaliacaoId)).thenReturn(Optional.of(avaliacao));

        // Act
        AvaliacaoResponseDTO resultado = avaliacaoService.buscarAvaliacaoPorId(avaliacaoId);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(avaliacaoId);
        verify(avaliacaoRepository).findById(avaliacaoId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando avaliação não encontrada")
    void buscarAvaliacaoPorId_DeveLancarExcecao_QuandoIdInexistente() {
        // Arrange
        when(avaliacaoRepository.findById(avaliacaoId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> avaliacaoService.buscarAvaliacaoPorId(avaliacaoId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Avaliação não encontrada com ID: " + avaliacaoId);

        verify(avaliacaoRepository).findById(avaliacaoId);
    }

    @Test
    @DisplayName("Deve marcar notificação como enviada")
    void marcarNotificacaoEnviada_DeveAtualizarFlag_QuandoAvaliacaoExistente() {
        // Arrange
        when(avaliacaoRepository.findById(avaliacaoId)).thenReturn(Optional.of(avaliacao));
        when(avaliacaoRepository.save(any(Avaliacao.class))).thenReturn(avaliacao);

        // Act
        avaliacaoService.marcarNotificacaoEnviada(avaliacaoId);

        // Assert
        verify(avaliacaoRepository).findById(avaliacaoId);
        verify(avaliacaoRepository).save(avaliacao);
        assertThat(avaliacao.getNotificacaoEnviada()).isTrue();
    }

    @Test
    @DisplayName("Deve lançar exceção ao marcar notificação para avaliação inexistente")
    void marcarNotificacaoEnviada_DeveLancarExcecao_QuandoAvaliacaoInexistente() {
        // Arrange
        when(avaliacaoRepository.findById(avaliacaoId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> avaliacaoService.marcarNotificacaoEnviada(avaliacaoId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Avaliação não encontrada com ID: " + avaliacaoId);

        verify(avaliacaoRepository).findById(avaliacaoId);
        verify(avaliacaoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve classificar urgência manualmente")
    void classificarUrgenciaManual_DeveAtualizarUrgencia_QuandoAvaliacaoExistente() {
        // Arrange
        when(avaliacaoRepository.findById(avaliacaoId)).thenReturn(Optional.of(avaliacao));
        when(avaliacaoRepository.save(any(Avaliacao.class))).thenReturn(avaliacao);

        // Act
        avaliacaoService.classificarUrgenciaManual(avaliacaoId, Urgencia.MEDIO);

        // Assert
        verify(avaliacaoRepository).findById(avaliacaoId);
        verify(avaliacaoRepository).save(avaliacao);
        assertThat(avaliacao.getUrgencia()).isEqualTo(Urgencia.MEDIO);
    }

    @Test
    @DisplayName("Deve lançar exceção ao classificar urgência para avaliação inexistente")
    void classificarUrgenciaManual_DeveLancarExcecao_QuandoAvaliacaoInexistente() {
        // Arrange
        when(avaliacaoRepository.findById(avaliacaoId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> avaliacaoService.classificarUrgenciaManual(avaliacaoId, Urgencia.CRITICO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Avaliação não encontrada com ID: " + avaliacaoId);

        verify(avaliacaoRepository).findById(avaliacaoId);
        verify(avaliacaoRepository, never()).save(any());
    }
}
