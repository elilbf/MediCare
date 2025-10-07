package com.scheduler.userservice.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JWTResponse {
    private String token;
    private Long id;
    private String username;
    private String roles;

    public JWTResponse(String token, Long id, String username, String roles) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.roles = roles;
    }

    // Getters
    public String getToken() { return token; }
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getRoles() { return roles; }
}