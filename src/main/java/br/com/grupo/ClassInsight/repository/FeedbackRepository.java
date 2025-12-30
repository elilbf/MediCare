package br.com.grupo.ClassInsight.repository;

import br.com.grupo.ClassInsight.model.Feedback;
import br.com.grupo.ClassInsight.model.Turma;
import br.com.grupo.ClassInsight.model.Usuario;
import br.com.grupo.ClassInsight.model.StatusFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByTurma(Turma turma);
    List<Feedback> findByAluno(Usuario aluno);
    List<Feedback> findByStatus(StatusFeedback status);
    List<Feedback> findByTurmaAndAluno(Turma turma, Usuario aluno);
    List<Feedback> findByTurmaAndStatus(Turma turma, StatusFeedback status);
}
