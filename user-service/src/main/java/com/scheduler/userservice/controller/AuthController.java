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
        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);
        Usuario usuario = usuarioRepository.findByUsername(loginRequest.getUsername()).orElse(null);
        Long id = usuario != null ? usuario.getId() : null;
        return ResponseEntity.ok(new JWTResponse(
            jwt, 
            id,
            userDetails.getUsername(),
            userDetails.getAuthorities().toString()
        ));
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestBody String token) {
        try {
            var claims = jwtUtil.extractAllClaims(token);
            String username = claims.getSubject();
            Object rolesObj = claims.get("roles");
            String roles;
            if (rolesObj instanceof String) {
                roles = (String) rolesObj;
            } else if (rolesObj instanceof java.util.List<?>) {
                @SuppressWarnings("unchecked")
                java.util.List<Object> rolesList = (java.util.List<Object>) rolesObj;
                roles = String.join(",", rolesList.stream().map(Object::toString).toList());
            } else {
                roles = "";
            }
            // TODO: Evitar consulta desnecessária ao banco passando ID no token
            Usuario usuario = usuarioRepository.findByUsername(username).orElse(null);
            Long id = usuario != null ? usuario.getId() : null;
            return ResponseEntity.ok(new JWTResponse(token, id, username, roles));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(new ErrorResponse("Token inválido ou expirado"));
        }
    }
}