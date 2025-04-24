package com.example.famouschat.Service;
//I ChatService klassen bliver brugerens besked sendt til OpenAI’s API gennem et HTTP POST-kald ved hjælp af WebClient
// (fra Spring Framework). Denne forespørgsel bruger en specifik API-model, som i mit tilfælde er gpt-3.5-turbo
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
//Injection af afhængigheder (Dependency Injection)
    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private WebClient webClient;

    // håndtere konfigurationer, som f.eks. API-nøgler,
    @Value("${api.key}")
    private String apiKey;

    public ChatService(ChatSessionRepository chatSessionRepository,
                       ChatMessageRepository chatMessageRepository) {
        this.chatSessionRepository = chatSessionRepository;
        this.chatMessageRepository = chatMessageRepository;
    }

//webclient er initialiseret med OpenAI’s API-base-URL og den nødvendige API-nøgle,
// som bruges til autentificering, når der foretages API-kald
    @PostConstruct
    public void init() {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }
    //Oprettelse af en session:
    public ChatSession createSession(String figureName) {
        ChatSession session = new ChatSession();
        session.setFigureName(figureName);
        return chatSessionRepository.save(session);
    }
    //Håndtering af en besked fra brugeren:
    public List<ChatSession> getAllSessions() {
        return chatSessionRepository.findAll();
    }

    public Optional<ChatSession> getSession(Long id) {
        return chatSessionRepository.findById(id);
    }

    public String handleMessage(ChatRequestDTO requestDTO) {
        Long sessionId = requestDTO.getSessionId();
        String userMessage = requestDTO.getMessage();

// Find sessionen i databasen ved hjælp af sessionId
        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        //her oprettes en ChatMessage objekt for brugerens besked og gem det i databasen
        ChatMessage userMsg = new ChatMessage();
        userMsg.setRole("user");
        userMsg.setContent(userMessage);
        userMsg.setSession(session);
        chatMessageRepository.save(userMsg);

        // Forbered prompt til OpenAI
        //prompt sikrer, at AI’en svarer på en passende måde
        ChatGPTRequest.Message systemPrompt = new ChatGPTRequest.Message(
                "system", "Du er " + session.getFigureName() + ". Svar som dem.");
        ChatGPTRequest.Message userPrompt = new ChatGPTRequest.Message("user", userMessage);

        ChatGPTRequest openaiRequest = new ChatGPTRequest(
                "gpt-3.5-turbo",// OpenAI GPT-3.5 model er modellen som bruges
                List.of(systemPrompt, userPrompt),
                0.7, // Temperatur: Kontrollerer graden af kreativitet i svarene (0.7 betyder moderat kreativitet)
                1.0 //// top_p: Kontrollerer diversiteten i svarene (1.0 betyder maksimal diversitet)
        );

        //  Kald til OpenAI API’en:
        //WebClient sender en POST-anmodning til OpenAI API’en
        //ansvarlig for at sende en anmodning til OpenAI’s API og hente et svar, som bliver behandlet i applikationen.
        ChatGPTResponse aiResponse = webClient.post()
                .uri("/chat/completions")//// API endpoint for sende anmodnung hente svar
                .contentType(MediaType.APPLICATION_JSON)// Sæt content-type til JSON
                .bodyValue(openaiRequest)
                .retrieve()// Udfør kaldet og hent respons
                .bodyToMono(ChatGPTResponse.class)//// Konverter responsen til ChatGPTResponse objekt
                .block();

        String aiText = aiResponse.getChoices().get(0).getMessage().getContent();

        // Opret en ChatMessage objekt for AI-svaret og gem det i databasen
        ChatMessage botMsg = new ChatMessage();
        botMsg.setRole("assistant");
        botMsg.setContent(aiText);
        botMsg.setSession(session);
        chatMessageRepository.save(botMsg);

        return aiText;//// Returnér AI-svaret som en tekst til frontend
    }
}
