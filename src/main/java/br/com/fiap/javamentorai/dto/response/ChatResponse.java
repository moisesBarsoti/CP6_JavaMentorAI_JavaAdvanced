package br.com.fiap.javamentorai.dto.response;

import java.time.LocalDateTime;

public record ChatResponse(

        Long id,
        String resposta,
        String provedor,
        String modelo,
        LocalDateTime dataHora

) {}