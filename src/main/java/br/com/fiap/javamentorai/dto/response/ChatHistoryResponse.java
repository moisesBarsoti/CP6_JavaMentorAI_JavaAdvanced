package br.com.fiap.javamentorai.dto.response;

import java.time.LocalDateTime;

public record ChatHistoryResponse(

    Long id,
    String pergunta,
    String resposta,
    String provedor,
    String modelo,
    LocalDateTime dataHora

) {}