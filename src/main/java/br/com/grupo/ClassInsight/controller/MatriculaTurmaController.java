package br.com.grupo.ClassInsight.controller;

import br.com.grupo.ClassInsight.model.MatriculaTurma;
import br.com.grupo.ClassInsight.service.MatriculaTurmaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/matriculas")
@CrossOrigin(origins = "*", maxAge = 3600)
public class MatriculaTurmaController {
    
    private final MatriculaTurmaService matriculaTurmaService;
    
    public MatriculaTurmaController(MatriculaTurmaService matriculaTurmaService) {
        this.matriculaTurmaService = matriculaTurmaService;
    }
    
    @PostMapping("/aluno/{alunoId}/turma/{turmaId}")
    public ResponseEntity<MatriculaTurma> matricularAluno(@PathVariable Long alunoId, @PathVariable Long turmaId) {
        MatriculaTurma matricula = matriculaTurmaService.matricularAluno(alunoId, turmaId);
        return ResponseEntity.status(HttpStatus.CREATED).body(matricula);
    }
    
    @DeleteMapping("/aluno/{alunoId}/turma/{turmaId}")
    public ResponseEntity<Void> desmatricular(@PathVariable Long alunoId, @PathVariable Long turmaId) {
        matriculaTurmaService.desmatricular(alunoId, turmaId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/aluno/{alunoId}")
    public ResponseEntity<List<MatriculaTurma>> listarMatriculasPorAluno(@PathVariable Long alunoId) {
        List<MatriculaTurma> matriculas = matriculaTurmaService.listarMatriculasPorAluno(alunoId);
        return ResponseEntity.ok(matriculas);
    }
    
    @GetMapping("/turma/{turmaId}")
    public ResponseEntity<List<MatriculaTurma>> listarMatriculasPorTurma(@PathVariable Long turmaId) {
        List<MatriculaTurma> matriculas = matriculaTurmaService.listarMatriculasPorTurma(turmaId);
        return ResponseEntity.ok(matriculas);
    }
    
    @GetMapping("/aluno/{alunoId}/turma/{turmaId}/verificar")
    public ResponseEntity<Boolean> verificarMatricula(@PathVariable Long alunoId, @PathVariable Long turmaId) {
        boolean matriculado = matriculaTurmaService.verificarMatricula(alunoId, turmaId);
        return ResponseEntity.ok(matriculado);
    }
}
