package it.engsoft.risiko.dto;

import java.util.List;

public class TrisDTO {
    private String giocatore;
    private List<Integer> tris;

    public TrisDTO(String giocatore, List<Integer> tris) {
        if(giocatore == null || giocatore.trim().isEmpty())
            throw new RuntimeException("Nome giocatore non valido.");
        this.giocatore = giocatore;

        if(tris.size() != 3)
            throw new RuntimeException("Tris non valido");
        this.tris = tris;
    }

    public String getGiocatore() {
        return giocatore;
    }

    public List<Integer> getTris() {
        return tris;
    }
}
