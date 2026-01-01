package br.com.grupo.ClassInsight.controller;

import br.com.grupo.ClassInsight.dto.UsuarioCriacaoDTO;
import br.com.grupo.ClassInsight.dto.UsuarioDTO;
import br.com.grupo.ClassInsight.model.TipoUsuario;
import br.com.grupo.ClassInsight.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "Usuários", description = "API para gerenciamento de usuários do sistema")
@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UsuarioController {
    
    private final UsuarioService usuarioService;
    
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }
    
    @Operation(summary = "Criar usuário", description = "Cria um novo usuário no sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping
    public ResponseEntity<UsuarioDTO> criarUsuario(@Valid @RequestBody UsuarioCriacaoDTO dto) {
        UsuarioDTO usuario = usuarioService.criarUsuario(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }
    
    @Operation(summary = "Obter usuário por ID", description = "Retorna um usuário específico pelo seu ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> obterUsuarioPorId(
            @Parameter(description = "ID do usuário") @PathVariable Long id) {
        UsuarioDTO usuario = usuarioService.obterUsuarioPorId(id);
        return ResponseEntity.ok(usuario);
    }
    
    @Operation(summary = "Obter usuário por e-mail", description = "Retorna um usuário específico pelo seu e-mail")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<UsuarioDTO> obterUsuarioPorEmail(
            @Parameter(description = "E-mail do usuário") @PathVariable String email) {
        UsuarioDTO usuario = usuarioService.obterUsuarioPorEmail(email);
        return ResponseEntity.ok(usuario);
    }
    
    @Operation(summary = "Listar todos os usuários", description = "Retorna uma lista com todos os usuários cadastrados")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de usuários retornada")
    })
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listarTodos() {
        List<UsuarioDTO> usuarios = usuarioService.listarTodos();
        return ResponseEntity.ok(usuarios);
    }
    
    @Operation(summary = "Listar usuários por tipo", description = "Retorna uma lista de usuários filtrada por tipo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de usuários retornada")
    })
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<UsuarioDTO>> listarPorTipo(
            @Parameter(description = "Tipo do usuário") @PathVariable TipoUsuario tipo) {
        List<UsuarioDTO> usuarios = usuarioService.listarPorTipo(tipo);
        return ResponseEntity.ok(usuarios);
    }
    
    @Operation(summary = "Atualizar usuário", description = "Atualiza os dados de um usuário existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> atualizarUsuario(
            @Parameter(description = "ID do usuário") @PathVariable Long id, 
            @Valid @RequestBody UsuarioCriacaoDTO dto) {
        UsuarioDTO usuario = usuarioService.atualizarUsuario(id, dto);
        return ResponseEntity.ok(usuario);
    }
    
    @Operation(summary = "Deletar usuário", description = "Remove um usuário do sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Usuário deletado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarUsuario(
            @Parameter(description = "ID do usuário") @PathVariable Long id) {
        usuarioService.deletarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}
