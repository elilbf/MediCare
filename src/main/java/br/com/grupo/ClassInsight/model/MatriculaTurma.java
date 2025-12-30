package br.com.grupo.ClassInsight.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "matriculas_turmas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatriculaTurma {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "aluno_id", nullable = false)
    private Usuario aluno;
    
    @ManyToOne
    @JoinColumn(name = "turma_id", nullable = false)
    private Turma turma;
    
    @Column(name = "data_matricula", nullable = false)
    private LocalDateTime dataMatricula;
    
    @Column(name = "ativa", nullable = false)
    private boolean ativa = true;
    
    @PrePersist
    protected void onCreate() {
        dataMatricula = LocalDateTime.now();
    }
}
