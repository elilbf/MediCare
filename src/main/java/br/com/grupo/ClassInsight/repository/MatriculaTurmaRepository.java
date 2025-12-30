package br.com.grupo.ClassInsight.repository;

import br.com.grupo.ClassInsight.model.MatriculaTurma;
import br.com.grupo.ClassInsight.model.Turma;
import br.com.grupo.ClassInsight.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface MatriculaTurmaRepository extends JpaRepository<MatriculaTurma, Long> {
    Optional<MatriculaTurma> findByAlunoAndTurma(Usuario aluno, Turma turma);
    List<MatriculaTurma> findByAluno(Usuario aluno);
    List<MatriculaTurma> findByTurma(Turma turma);
    List<MatriculaTurma> findByAlunoAndAtivaTrue(Usuario aluno);
    List<MatriculaTurma> findByTurmaAndAtivaTrue(Turma turma);
}
