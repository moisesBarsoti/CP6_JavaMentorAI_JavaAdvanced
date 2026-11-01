package br.com.fiap.javamentorai.controller;

import br.com.fiap.javamentorai.dto.response.ChatHistoryResponse;
import br.com.fiap.javamentorai.service.ChatService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/historico")
@Tag(name = "Histórico", description = "Consulta do histórico de conversas do JavaMentor AI")
public class ChatHistoryController {

    private final ChatService chatService;

    public ChatHistoryController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping
    @Operation(
            summary = "Consultar histórico de conversas",
            description = "Retorna todas as perguntas e respostas armazenadas no PostgreSQL, ordenadas da mais recente para a mais antiga."
    )
    public ResponseEntity<List<ChatHistoryResponse>> getHistory() {

        List<ChatHistoryResponse> history = chatService.getHistory();

        return ResponseEntity.ok(history);
    }
}