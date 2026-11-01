package br.com.fiap.javamentorai.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatRequest(

    @NotBlank(message = "A mensagem não pode estar vazia")
    @Size(max = 2000, message = "A mensagem deve ter no máximo 2000 caracteres")
    String mensagem

) {}