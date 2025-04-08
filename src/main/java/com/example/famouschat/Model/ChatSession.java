package com.example.famouschat.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class ChatSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String figureName; // Navn på den historiske/personlige figur

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL)
    private List<ChatMessage> messages;

    public ChatSession() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters og setters

    public Long getId() {
        return id;
    }

    public String getFigureName() {
        return figureName;
    }

    public void setFigureName(String figureName) {
        this.figureName = figureName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
    }
}

