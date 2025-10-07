package com.scheduler.userservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scheduler.userservice.DTO.AtualizaUsuarioRequest;
import com.scheduler.userservice.DTO.CriaUsuarioRequest;
import com.scheduler.userservice.entities.Usuario;
import com.scheduler.userservice.service.UsuarioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/usuario")
@Tag(name = "Usuarios", description = "Endpoints para gerenciamento de usuários")
@Slf4j
public class UsuarioController {

    private final UsuarioService usuarioService;
    
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }
    
    @PostMapping("/criar")
    public ResponseEntity<Usuario> criarUsuario(@RequestBody CriaUsuarioRequest usuario) {
        log.info("Criando usuário: {}", usuario);
        if(usuario.getNome() == null || usuario.getEmail() == null || usuario.getSenha() == null || usuario.getUsername() == null) {
            throw new IllegalArgumentException("Nome, email, username e senha são obrigatórios");
        }
        Usuario novoUsuario = usuarioService.criarUsuario(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario);
    }
    
    @GetMapping("/buscar/{id}")
    public ResponseEntity<Usuario> buscarUsuarioPorId(@PathVariable Long id) {
        log.info("Buscando usuário por id: {}", id);
        Usuario usuario = usuarioService.buscarUsuarioPorId(id);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não encontrado para o id: " + id);
        }
        return ResponseEntity.ok(usuario);
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<Usuario> atualizarUsuario(@PathVariable Long id, @Valid @RequestBody AtualizaUsuarioRequest atualizaUsuarioRequest) {
        log.info("Atualizando usuário id {}: {}", id, atualizaUsuarioRequest);
        Usuario usuarioAtualizado = usuarioService.atualizarUsuario(id, atualizaUsuarioRequest);
        if (usuarioAtualizado == null) {
            throw new IllegalArgumentException("Usuário não encontrado para o id: " + id);
        }
        return ResponseEntity.ok(usuarioAtualizado);
    }

}
