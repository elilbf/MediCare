package br.com.grupo.ClassInsight.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;

public record RespostaFeedbackDTO(
    @NotBlank(message = "Resposta não pode estar vazia")
    String resposta
) {}
