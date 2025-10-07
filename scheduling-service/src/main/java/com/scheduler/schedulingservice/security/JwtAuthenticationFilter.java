package com.scheduler.schedulingservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final String USER_SERVICE_VALIDATE_URL = "http://user-service:8082/auth/validate";
    private final RestTemplate restTemplate = new RestTemplate();
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String jwt = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
        }
        if (jwt != null) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<String> entity = new HttpEntity<>(jwt, headers);
                ResponseEntity<JsonNode> resp = restTemplate.postForEntity(USER_SERVICE_VALIDATE_URL, entity, JsonNode.class);
                if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
                    Long userId = resp.getBody().has("id") && !resp.getBody().get("id").isNull() ? resp.getBody().get("id").asLong() : null;
                    String username = resp.getBody().get("username").asText();
                    String rolesStr = resp.getBody().get("roles").asText();
                    var roles = java.util.Arrays.asList(rolesStr.split(","));
                    var authorities = roles.stream().map(r -> new SimpleGrantedAuthority("ROLE_" + r.trim())).toList();
                    // O userId será usado como principal
                    var authToken = new UsernamePasswordAuthenticationToken(userId, null, authorities);
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    logger.info("JWT válido para usuário '{}', id: {}, roles: {}", username, userId, rolesStr);
                } else {
                    logger.warn("JWT inválido ou não autorizado para request {} {}", request.getMethod(), request.getRequestURI());
                }
            } catch (Exception e) {
                logger.warn("Falha ao validar JWT com user-service para request {} {}: {}", request.getMethod(), request.getRequestURI(), e.getMessage());
                SecurityContextHolder.clearContext();
            }
        } else {
            logger.warn("Requisição sem JWT ou formato inválido: {} {}", request.getMethod(), request.getRequestURI());
        }
        filterChain.doFilter(request, response);
    }
}
