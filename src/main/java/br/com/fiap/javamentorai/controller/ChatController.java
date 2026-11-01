package br.com.fiap.javamentorai.controller;

import br.com.fiap.javamentorai.dto.request.ChatRequest;
import br.com.fiap.javamentorai.dto.response.ChatResponse;
import br.com.fiap.javamentorai.service.ChatService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@Tag(name = "Chat", description = "Assistente de estudos Java com inteligência artificial")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    @Operation(
            summary = "Enviar pergunta para a IA",
            description = "Envia uma pergunta ao JavaMentor AI e registra a resposta no histórico."
    )
    public ResponseEntity<ChatResponse> sendMessage(@Valid @RequestBody ChatRequest request) {
        ChatResponse response = chatService.sendMessage(request);

        return ResponseEntity.ok(response);
    }
}