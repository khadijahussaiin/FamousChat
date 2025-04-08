package com.example.famouschat.Controller;

import com.example.famouschat.DTO.ChatRequestDTO;
import com.example.famouschat.DTO.NewSessionDTO;
import com.example.famouschat.Model.ChatSession;
import com.example.famouschat.Service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin // Tillader kald fra frontend (kan tilpasses)
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    // Opret ny session (med personnavn)
    @PostMapping("/session")
    public ResponseEntity<ChatSession> createSession(@RequestBody NewSessionDTO dto) {
        ChatSession session = chatService.createSession(dto.getFigureName());
        return ResponseEntity.ok(session);
    }

    // Hent specifik session og dens beskeder
    @GetMapping("/session/{id}")
    public ResponseEntity<ChatSession> getSession(@PathVariable Long id) {
        return chatService.getSession(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Hent alle tidligere sessions
    @GetMapping("/sessions")
    public ResponseEntity<List<ChatSession>> getAllSessions() {
        return ResponseEntity.ok(chatService.getAllSessions());
    }

    // Send besked og få svar fra OpenAI
    @PostMapping("/chat")
    public ResponseEntity<String> chat(@RequestBody ChatRequestDTO dto) {
        String response = chatService.handleMessage(dto);
        return ResponseEntity.ok(response);
    }
}

