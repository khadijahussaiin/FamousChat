package com.example.famouschat.DTO;
//ChatRequestDTO er DTO'er, fordi de kun indeholder de data, der er nødvendige for at sende og oprette en session.
// De er ikke bundet til databasen og har ikke relationer som model-klasser.
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

