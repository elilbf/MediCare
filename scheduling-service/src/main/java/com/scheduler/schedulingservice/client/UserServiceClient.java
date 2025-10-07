package com.scheduler.schedulingservice.client;

import com.scheduler.schedulingservice.dto.UsuarioDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component
public class UserServiceClient {

    private static final Logger log = LoggerFactory.getLogger(UserServiceClient.class);
    private final RestTemplate restTemplate;
    private final String userServiceUrl;

    public UserServiceClient(RestTemplate restTemplate, @Value("${user-service.url:http://localhost:8082}") String userServiceUrl) {
        this.restTemplate = restTemplate;
        this.userServiceUrl = userServiceUrl;
    }

    public UsuarioDto buscarUsuarioPorId(Long id) {
        try {
            String url = userServiceUrl + "/usuario/buscar/" + id;
            log.info("Chamando user-service para buscar usuário com ID: {}", id);
            return restTemplate.getForObject(url, UsuarioDto.class);
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Usuário com ID {} não encontrado no user-service", id);
            return null;
        }
    }

    public UsuarioDto buscarUsuarioFallback(Long id, Exception ex) {
        log.error("Circuit breaker ativo para user-service. Fallback executado para ID: {}. Erro: {}", id, ex.getMessage());
        return null;
    }
}
