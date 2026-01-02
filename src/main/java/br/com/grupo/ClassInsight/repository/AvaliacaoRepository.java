package br.com.grupo.ClassInsight.repository;

import br.com.grupo.ClassInsight.model.Avaliacao;
import br.com.grupo.ClassInsight.model.Urgencia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, UUID> {

    Page<Avaliacao> findByUrgencia(Urgencia urgencia, Pageable pageable);

    Page<Avaliacao> findByDataEnvioBetween(LocalDateTime dataInicio, LocalDateTime dataFim, Pageable pageable);

    Page<Avaliacao> findByUrgenciaAndDataEnvioBetween(Urgencia urgencia, LocalDateTime dataInicio, LocalDateTime dataFim, Pageable pageable);

    List<Avaliacao> findByDataEnvioBetween(LocalDateTime dataInicio, LocalDateTime dataFim);

    List<Avaliacao> findByUrgenciaAndDataEnvioBetweenOrderByDataEnvioDesc(Urgencia urgencia, LocalDateTime dataInicio, LocalDateTime dataFim);

    @Query("SELECT AVG(a.nota) FROM Avaliacao a WHERE a.dataEnvio BETWEEN :dataInicio AND :dataFim")
    Double calcularMediaPorPeriodo(@Param("dataInicio") LocalDateTime dataInicio, @Param("dataFim") LocalDateTime dataFim);

    @Query("SELECT COUNT(a) FROM Avaliacao a WHERE a.urgencia = :urgencia AND a.dataEnvio BETWEEN :dataInicio AND :dataFim")
    Long contarPorUrgenciaEPeriodo(@Param("urgencia") Urgencia urgencia, @Param("dataInicio") LocalDateTime dataInicio, @Param("dataFim") LocalDateTime dataFim);

    @Query("SELECT a FROM Avaliacao a WHERE a.urgencia = :urgencia AND a.dataEnvio BETWEEN :dataInicio AND :dataFim ORDER BY a.dataEnvio DESC")
    List<Avaliacao> encontrarPorUrgenciaEPeriodo(@Param("urgencia") Urgencia urgencia, @Param("dataInicio") LocalDateTime dataInicio, @Param("dataFim") LocalDateTime dataFim);

    @Query("SELECT COUNT(a) FROM Avaliacao a WHERE a.dataEnvio BETWEEN :dataInicio AND :dataFim")
    Long contarPorPeriodo(@Param("dataInicio") LocalDateTime dataInicio, @Param("dataFim") LocalDateTime dataFim);

    @Query("SELECT FUNCTION('DATE', a.dataEnvio) as dia, COUNT(a) as quantidade FROM Avaliacao a WHERE a.dataEnvio BETWEEN :dataInicio AND :dataFim GROUP BY FUNCTION('DATE', a.dataEnvio) ORDER BY dia")
    List<Object[]> contarPorDiaNoPeriodo(@Param("dataInicio") LocalDateTime dataInicio, @Param("dataFim") LocalDateTime dataFim);
}
