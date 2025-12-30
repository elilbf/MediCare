package br.com.grupo.ClassInsight.repository;

import br.com.grupo.ClassInsight.model.Turma;
import br.com.grupo.ClassInsight.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface TurmaRepository extends JpaRepository<Turma, Long> {
    Optional<Turma> findByCodigoTurma(String codigoTurma);
    List<Turma> findByProfessor(Usuario professor);
    List<Turma> findByAtivaTrue();
}
