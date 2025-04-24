package com.example.famouschat.DTO;
//NewSessionDTO er DTO'er, fordi de kun indeholder de data, der er nødvendige for at sende og oprette en session.
// De er ikke bundet til databasen og har ikke relationer som model-klasser.
public class NewSessionDTO {
    private String figureName;

    // Getter og setter
    public String getFigureName() {
        return figureName;
    }

    public void setFigureName(String figureName) {
        this.figureName = figureName;
    }
}

