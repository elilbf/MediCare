package com.scheduler.userservice.service;


import com.scheduler.userservice.DTO.CriaUsuarioRequest;
import com.scheduler.userservice.DTO.AtualizaUsuarioRequest;
import com.scheduler.userservice.entities.Usuario;
import com.scheduler.userservice.repositories.UsuarioRepository;
import com.scheduler.userservice.security.SecurityConfig;

import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario criarUsuario(CriaUsuarioRequest usuario) {
        
        SecurityConfig securityConfig = new SecurityConfig();

        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(usuario.getNome());
        novoUsuario.setEmail(usuario.getEmail());
        novoUsuario.setSenha(securityConfig.passwordEncoder().encode(usuario.getSenha()));
        novoUsuario.setUsername(usuario.getUsername());
        novoUsuario.setRoles(usuario.getRoles());

        if(usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        if(usuarioRepository.findByUsername(usuario.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username já cadastrado");
        }

        try{        
            return usuarioRepository.save(novoUsuario);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar usuário: " + e.getMessage());
        }

    }

    public Usuario buscarUsuarioPorId(Long id) {
        if(id == null || id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }

        return usuarioRepository.findById(id).orElse(null);
    }

    public Usuario atualizarUsuario(Long id, AtualizaUsuarioRequest atualizaUsuarioRequest) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }

        Usuario usuario = usuarioRepository.findById(id).orElse(null);

        if (usuario == null) {
            return null;
        }

        if (atualizaUsuarioRequest.getNome() != null) {
            usuario.setNome(atualizaUsuarioRequest.getNome());
        }

        if (atualizaUsuarioRequest.getEmail() != null) {
            usuario.setEmail(atualizaUsuarioRequest.getEmail());
        }

        if (atualizaUsuarioRequest.getSenha() != null) {
            SecurityConfig securityConfig = new SecurityConfig();
            usuario.setSenha(securityConfig.passwordEncoder().encode(atualizaUsuarioRequest.getSenha()));
        }

        if (atualizaUsuarioRequest.getUsername() != null) {
            usuario.setUsername(atualizaUsuarioRequest.getUsername());
        }

        if (atualizaUsuarioRequest.getRoles() != null && !atualizaUsuarioRequest.getRoles().isEmpty()) {
            usuario.setRoles(atualizaUsuarioRequest.getRoles());
        }

        try {
            return usuarioRepository.save(usuario);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar usuário: " + e.getMessage());
        }
    }
}
