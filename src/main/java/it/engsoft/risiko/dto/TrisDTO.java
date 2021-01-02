package it.engsoft.risiko.dto;

import java.util.List;

public class TrisDTO {
    private String giocatore;
    private List<Long> tris;

    public TrisDTO(String giocatore, List<Long> tris) {
        if(giocatore == null || giocatore.trim().isEmpty())
            throw new RuntimeException("Nome giocatore non valido.");
        this.giocatore = giocatore;

        if(tris == null)
            throw new RuntimeException("Tris non valido");
        this.tris = tris;
    }

    public String getGiocatore() {
        return giocatore;
    }

    public List<Long> getTris() {
        return tris;
    }
}
