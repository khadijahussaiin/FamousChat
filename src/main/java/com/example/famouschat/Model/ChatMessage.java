package com.example.famouschat.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String role; // "user" eller "assistant"

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "session_id")
    private ChatSession session;

    public ChatMessage() {
        this.timestamp = LocalDateTime.now();
    }

    // Getters og setters

    public Long getId() {
        return id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public ChatSession getSession() {
        return session;
    }

    public void setSession(ChatSession session) {
        this.session = session;
    }
}

