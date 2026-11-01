package br.com.fiap.javamentorai.service;

import br.com.fiap.javamentorai.dto.request.ChatRequest;
import br.com.fiap.javamentorai.dto.response.ChatHistoryResponse;
import br.com.fiap.javamentorai.dto.response.ChatResponse;
import br.com.fiap.javamentorai.entity.ChatHistory;
import br.com.fiap.javamentorai.repository.ChatHistoryRepository;
import br.com.fiap.javamentorai.exception.AiProviderException;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {

    private static final String SYSTEM_PROMPT = """
            Você é o JavaMentor AI, um assistente especializado
            no ensino da linguagem Java e de seu ecossistema.

            Suas responsabilidades são:
            - Responder sempre em português brasileiro.
            - Explicar conceitos de Java de maneira didática.
            - Ajudar com programação orientada a objetos.
            - Auxiliar no aprendizado de Spring Boot e APIs REST.
            - Fornecer exemplos de código quando necessário.
            - Priorizar explicações claras e objetivas.

            Responda apenas perguntas relacionadas a Java,
            programação e tecnologias do ecossistema Java.

            Caso a pergunta esteja fora desse domínio,
            informe educadamente sua especialidade.

            Quando não souber uma resposta, não invente informações.
            """;

    private final ChatClient chatClient;
    private final ChatHistoryRepository repository;
    private final String model;

    public ChatService(
            ChatClient.Builder builder,
            ChatHistoryRepository repository,
            @Value("${spring.ai.ollama.chat.model}") String model
    ) {
        this.chatClient = builder.build();
        this.repository = repository;
        this.model = model;
    }

    public ChatResponse sendMessage(ChatRequest request) {

        String question = request.mensagem().trim();

        String answer;

        try {
            answer = chatClient.prompt()
                    .system(SYSTEM_PROMPT)
                    .user(question)
                    .call()
                    .content();

        } catch (RuntimeException exception) {
            throw new AiProviderException(
                    "Não foi possível estabelecer comunicação com o provedor de IA.",
                    exception
            );
        }

        if (answer == null || answer.isBlank()) {
            throw new AiProviderException("O provedor de IA não retornou uma resposta.");
        }

        ChatHistory history = new ChatHistory();

        history.setQuestion(question);
        history.setAnswer(answer);
        history.setProvider("Ollama");
        history.setModel(model);

        ChatHistory savedHistory = repository.save(history);

        return new ChatResponse(
                savedHistory.getId(),
                savedHistory.getAnswer(),
                savedHistory.getProvider(),
                savedHistory.getModel(),
                savedHistory.getCreatedAt()
        );
    }

    public List<ChatHistoryResponse> getHistory() {

        return repository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(history -> new ChatHistoryResponse(
                        history.getId(),
                        history.getQuestion(),
                        history.getAnswer(),
                        history.getProvider(),
                        history.getModel(),
                        history.getCreatedAt()
                )).toList();
    }
}