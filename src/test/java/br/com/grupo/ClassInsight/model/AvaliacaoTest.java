package br.com.grupo.ClassInsight.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes da Entidade Avaliacao")
class AvaliacaoTest {

    private Avaliacao avaliacao;

    @BeforeEach
    void setUp() {
        avaliacao = Avaliacao.builder()
                .descricao("Aula excelente, professor muito didático!")
                .nota(9)
                .build();
    }

    @Test
    @DisplayName("Deve classificar urgência como CRITICO quando nota < 5")
    void classificarUrgencia_DeveRetornarCritico_QuandoNotaMenorQue5() {
        // Arrange
        avaliacao.setNota(2);

        // Act
        avaliacao.classificarUrgencia();

        // Assert
        assertThat(avaliacao.getUrgencia()).isEqualTo(Urgencia.CRITICO);
    }

    @Test
    @DisplayName("Deve classificar urgência como MEDIO quando nota entre 5 e 6")
    void classificarUrgencia_DeveRetornarMedio_QuandoNotaEntre5E6() {
        // Arrange
        avaliacao.setNota(6);

        // Act
        avaliacao.classificarUrgencia();

        // Assert
        assertThat(avaliacao.getUrgencia()).isEqualTo(Urgencia.MEDIO);
    }

    @Test
    @DisplayName("Deve classificar urgência como POSITIVO quando nota >= 7")
    void classificarUrgencia_DeveRetornarPositivo_QuandoNotaMaiorOuIgual7() {
        // Arrange
        avaliacao.setNota(8);

        // Act
        avaliacao.classificarUrgencia();

        // Assert
        assertThat(avaliacao.getUrgencia()).isEqualTo(Urgencia.POSITIVO);
    }

    @Test
    @DisplayName("Deve classificar corretamente notas limite")
    void classificarUrgencia_DeveClassificarCorretamente_NotasLimite() {
        // Teste nota 4 - CRITICO
        avaliacao.setNota(4);
        avaliacao.classificarUrgencia();
        assertThat(avaliacao.getUrgencia()).isEqualTo(Urgencia.CRITICO);

        // Teste nota 5 - MEDIO
        avaliacao.setNota(5);
        avaliacao.classificarUrgencia();
        assertThat(avaliacao.getUrgencia()).isEqualTo(Urgencia.MEDIO);

        // Teste nota 6 - MEDIO
        avaliacao.setNota(6);
        avaliacao.classificarUrgencia();
        assertThat(avaliacao.getUrgencia()).isEqualTo(Urgencia.MEDIO);

        // Teste nota 7 - POSITIVO
        avaliacao.setNota(7);
        avaliacao.classificarUrgencia();
        assertThat(avaliacao.getUrgencia()).isEqualTo(Urgencia.POSITIVO);
    }

    @Test
    @DisplayName("Deve preencher dataEnvio no prePersist")
    void prePersist_DevePreencherDataEnvio_QuandoNula() {
        // Arrange
        avaliacao.setDataEnvio(null);

        // Act
        avaliacao.prePersist();

        // Assert
        assertThat(avaliacao.getDataEnvio()).isNotNull();
        assertThat(avaliacao.getDataEnvio()).isBefore(LocalDateTime.now().plusSeconds(1));
    }

    @Test
    @DisplayName("Deve classificar urgência no prePersist quando nula")
    void prePersist_DeveClassificarUrgencia_QuandoNula() {
        // Arrange
        avaliacao.setNota(3);
        avaliacao.setUrgencia(null);

        // Act
        avaliacao.prePersist();

        // Assert
        assertThat(avaliacao.getUrgencia()).isEqualTo(Urgencia.CRITICO);
    }

    @Test
    @DisplayName("Não deve alterar dataEnvio no prePersist quando já preenchida")
    void prePersist_NaoDeveAlterarDataEnvio_QuandoJaPreenchida() {
        // Arrange
        LocalDateTime dataOriginal = LocalDateTime.now().minusHours(1);
        avaliacao.setDataEnvio(dataOriginal);

        // Act
        avaliacao.prePersist();

        // Assert
        assertThat(avaliacao.getDataEnvio()).isEqualTo(dataOriginal);
    }

    @Test
    @DisplayName("Deve manter urgência existente no prePersist")
    void prePersist_NaoDeveAlterarUrgencia_QuandoJaPreenchida() {
        // Arrange
        avaliacao.setNota(8);
        avaliacao.setUrgencia(Urgencia.MEDIO); // Urgência diferente da classificação automática

        // Act
        avaliacao.prePersist();

        // Assert
        assertThat(avaliacao.getUrgencia()).isEqualTo(Urgencia.MEDIO);
    }

    @Test
    @DisplayName("Builder deve criar entidade corretamente")
    void builder_DeveCriarEntidadeCorretamente_QuandoUsado() {
        // Arrange & Act
        Avaliacao novaAvaliacao = Avaliacao.builder()
                .descricao("Teste builder")
                .nota(10)
                .urgencia(Urgencia.POSITIVO)
                .dataEnvio(LocalDateTime.now())
                .notificacaoEnviada(true)
                .processado(true)
                .build();

        // Assert
        assertThat(novaAvaliacao.getDescricao()).isEqualTo("Teste builder");
        assertThat(novaAvaliacao.getNota()).isEqualTo(10);
        assertThat(novaAvaliacao.getUrgencia()).isEqualTo(Urgencia.POSITIVO);
        assertThat(novaAvaliacao.getNotificacaoEnviada()).isTrue();
        assertThat(novaAvaliacao.getProcessado()).isTrue();
        assertThat(novaAvaliacao.getDataEnvio()).isNotNull();
    }
}
