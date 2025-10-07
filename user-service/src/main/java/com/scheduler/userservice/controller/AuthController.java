package com.scheduler.userservice.controller;

import com.scheduler.userservice.DTO.ErrorResponse;
import com.scheduler.userservice.DTO.JWTResponse;
import com.scheduler.userservice.DTO.LoginRequest;
import com.scheduler.userservice.security.JwtUtil;
import com.scheduler.userservice.entities.Usuario;
import com.scheduler.userservice.repositories.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "Endpoints para autenticação JWT")
@Slf4j
@RequiredArgsConstructor  // Lombok gera construtor com dependências final
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    @Operation(summary = "Realizar login", description = "Autentica usuário e retorna token JWT")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(), 
                    loginRequest.getPassword()
                )
            );
            log.info("Login realizado com sucesso para o usuário: {}", loginRequest.getUsername());
        } catch (BadCredentialsException e) {
            log.warn("Tentativa de login com credenciais inválidas para usuário: {}", loginRequest.getUsername());
            return ResponseEntity.badRequest().body(new ErrorResponse("Credenciais inválidas"));
        } catch (Exception e) {
            log.error("Erro ao realizar login para usuário: {}", loginRequest.getUsername(), e);
            return ResponseEntity.badRequest().body(new ErrorResponse("Erro ao realizar login"));
        }

        final Usuario user = usuarioRepository.findByUsername(loginRequest.getUsername()).orElse(null);
        final String jwt = jwtUtil.generateToken(user.getUsername(), user.getId(), user.getRoles());

        return ResponseEntity.ok(new JWTResponse(
            jwt, 
            user.getUsername(),
            user.getRoles().toString()
        ));
    }
}