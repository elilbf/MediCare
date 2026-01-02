package br.com.grupo.ClassInsight.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "avaliacoes")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Avaliacao {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "descricao", nullable = false, length = 1000)
    @NotBlank(message = "Descrição é obrigatória")
    @Size(min = 10, max = 1000, message = "Descrição deve ter entre 10 e 1000 caracteres")
    private String descricao;

    @Column(name = "nota", nullable = false)
    @Min(value = 0, message = "Nota deve ser no mínimo 0")
    @Max(value = 10, message = "Nota deve ser no máximo 10")
    private Integer nota;

    @Enumerated(EnumType.STRING)
    @Column(name = "urgencia", nullable = false)
    private Urgencia urgencia;

    @CreatedDate
    @Column(name = "data_envio", nullable = false, updatable = false)
    private LocalDateTime dataEnvio;

    @Column(name = "notificacao_enviada", nullable = false)
    @Builder.Default
    private Boolean notificacaoEnviada = false;

    @Column(name = "processado", nullable = false)
    @Builder.Default
    private Boolean processado = false;

    @PrePersist
    public void prePersist() {
        if (urgencia == null) {
            classificarUrgencia();
        }
        if (dataEnvio == null) {
            dataEnvio = LocalDateTime.now();
        }
    }

    public void classificarUrgencia() {
        if (nota < 5) {
            this.urgencia = Urgencia.CRITICO;
        } else if (nota < 7) {
            this.urgencia = Urgencia.MEDIO;
        } else {
            this.urgencia = Urgencia.POSITIVO;
        }
    }
}
