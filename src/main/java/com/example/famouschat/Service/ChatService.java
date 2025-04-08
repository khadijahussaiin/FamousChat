package com.example.famouschat.Service;

import com.example.famouschat.Model.ChatMessage;
import com.example.famouschat.Model.ChatSession;
import com.example.famouschat.Repository.ChatMessageRepository;
import com.example.famouschat.Repository.ChatSessionRepository;
import com.example.famouschat.DTO.ChatRequestDTO;
import com.example.famouschat.Model.ChatGPTRequest;
import com.example.famouschat.Model.ChatGPTResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

@Service
public class ChatService {

    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private WebClient webClient;

    @Value("${api.key}")
    private String apiKey;

    public ChatService(ChatSessionRepository chatSessionRepository,
                       ChatMessageRepository chatMessageRepository) {
        this.chatSessionRepository = chatSessionRepository;
        this.chatMessageRepository = chatMessageRepository;
    }

    @PostConstruct
    public void init() {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    public ChatSession createSession(String figureName) {
        ChatSession session = new ChatSession();
        session.setFigureName(figureName);
        return chatSessionRepository.save(session);
    }

    public List<ChatSession> getAllSessions() {
        return chatSessionRepository.findAll();
    }

    public Optional<ChatSession> getSession(Long id) {
        return chatSessionRepository.findById(id);
    }

    public String handleMessage(ChatRequestDTO requestDTO) {
        Long sessionId = requestDTO.getSessionId();
        String userMessage = requestDTO.getMessage();

        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        // Gem brugerbesked
        ChatMessage userMsg = new ChatMessage();
        userMsg.setRole("user");
        userMsg.setContent(userMessage);
        userMsg.setSession(session);
        chatMessageRepository.save(userMsg);

        // Forbered prompt til OpenAI
        ChatGPTRequest.Message systemPrompt = new ChatGPTRequest.Message(
                "system", "Du er " + session.getFigureName() + ". Svar som dem.");
        ChatGPTRequest.Message userPrompt = new ChatGPTRequest.Message("user", userMessage);

        ChatGPTRequest openaiRequest = new ChatGPTRequest(
                "gpt-3.5-turbo",
                List.of(systemPrompt, userPrompt),
                0.7,
                1.0
        );

        // Kald OpenAI API
        ChatGPTResponse aiResponse = webClient.post()
                .uri("/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(openaiRequest)
                .retrieve()
                .bodyToMono(ChatGPTResponse.class)
                .block();

        String aiText = aiResponse.getChoices().get(0).getMessage().getContent();

        // Gem AI-svar
        ChatMessage botMsg = new ChatMessage();
        botMsg.setRole("assistant");
        botMsg.setContent(aiText);
        botMsg.setSession(session);
        chatMessageRepository.save(botMsg);

        return aiText;
    }
}
