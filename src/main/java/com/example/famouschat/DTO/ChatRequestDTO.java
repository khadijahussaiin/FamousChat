package com.example.famouschat.DTO;

public class ChatRequestDTO {
    private Long sessionId;
    private String message;

    // Getters og setters
    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

