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

    public JWTResponse(String token, String username, String roles) {
        this.token = token;
        this.username = username;
        this.roles = roles;
    }
}