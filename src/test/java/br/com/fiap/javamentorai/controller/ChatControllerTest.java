package br.com.fiap.javamentorai.controller;

import br.com.fiap.javamentorai.dto.request.ChatRequest;
import br.com.fiap.javamentorai.exception.AiProviderException;
import br.com.fiap.javamentorai.service.ChatService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChatController.class)
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChatService chatService;

    @Test
    void shouldReturnBadRequestWhenMessageIsBlank() throws Exception {

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"mensagem\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail")
                        .value("A mensagem não pode estar vazia"));
    }

    @Test
    void shouldReturnServiceUnavailableWhenAiFails() throws Exception {

        when(chatService.sendMessage(any(ChatRequest.class)))
                .thenThrow(new AiProviderException(
                        "Não foi possível estabelecer comunicação com o provedor de IA."
                ));

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "mensagem": "Explique polimorfismo em Java."
                                }
                                """))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value(503));
    }
}