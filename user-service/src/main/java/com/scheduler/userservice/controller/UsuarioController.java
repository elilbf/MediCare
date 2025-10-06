package com.scheduler.userservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scheduler.userservice.DTO.CriaUsuarioRequest;
import com.scheduler.userservice.entities.Usuario;
import com.scheduler.userservice.service.UsuarioService;
import io.swagger.v3.oas.annotations.tags.Tag;
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
        
        try{
            Usuario NovoUsuario = usuarioService.criarUsuario(usuario);     
            return ResponseEntity.status(HttpStatus.CREATED).body(NovoUsuario);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

    }
    
    @GetMapping("/buscar/{id}")
    public ResponseEntity<Usuario> buscarUsuarioPorId(@PathVariable Long id) {
        log.info("Buscando usuário por id: {}", id);
        try {
            Usuario usuario = usuarioService.buscarUsuarioPorId(id);

            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            
            return ResponseEntity.ok(usuario);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

}
