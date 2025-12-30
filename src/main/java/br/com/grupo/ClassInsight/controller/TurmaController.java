package br.com.grupo.ClassInsight.controller;

import br.com.grupo.ClassInsight.dto.TurmaCriacaoDTO;
import br.com.grupo.ClassInsight.dto.TurmaDTO;
import br.com.grupo.ClassInsight.service.TurmaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/turmas")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TurmaController {
    
    private final TurmaService turmaService;
    
    public TurmaController(TurmaService turmaService) {
        this.turmaService = turmaService;
    }
    
    @PostMapping
    public ResponseEntity<TurmaDTO> criarTurma(@Valid @RequestBody TurmaCriacaoDTO dto) {
        TurmaDTO turma = turmaService.criarTurma(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(turma);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TurmaDTO> obterTurmaPorId(@PathVariable Long id) {
        TurmaDTO turma = turmaService.obterTurmaPorId(id);
        return ResponseEntity.ok(turma);
    }
    
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<TurmaDTO> obterTurmaPorCodigo(@PathVariable String codigo) {
        TurmaDTO turma = turmaService.obterTurmaPorCodigo(codigo);
        return ResponseEntity.ok(turma);
    }
    
    @GetMapping
    public ResponseEntity<List<TurmaDTO>> listarTodas() {
        List<TurmaDTO> turmas = turmaService.listarTodas();
        return ResponseEntity.ok(turmas);
    }
    
    @GetMapping("/professor/{professorId}")
    public ResponseEntity<List<TurmaDTO>> listarPorProfessor(@PathVariable Long professorId) {
        List<TurmaDTO> turmas = turmaService.listarPorProfessor(professorId);
        return ResponseEntity.ok(turmas);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<TurmaDTO> atualizarTurma(@PathVariable Long id, @Valid @RequestBody TurmaCriacaoDTO dto) {
        TurmaDTO turma = turmaService.atualizarTurma(id, dto);
        return ResponseEntity.ok(turma);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarTurma(@PathVariable Long id) {
        turmaService.deletarTurma(id);
        return ResponseEntity.noContent().build();
    }
}
