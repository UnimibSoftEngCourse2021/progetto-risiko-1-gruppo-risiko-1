package it.engsoft.risiko.rest.dto;

import it.engsoft.risiko.exceptions.DatiErratiException;

import java.util.List;

public class TrisDTO {
    private String giocatore;
    private List<Integer> tris;

    public TrisDTO(String giocatore, List<Integer> tris) {
        if(giocatore == null || giocatore.trim().isEmpty())
            throw new DatiErratiException("Dati errati: giocatore nullo o vuoto");
        this.giocatore = giocatore;

        if(tris.size() != 3)
            throw new DatiErratiException("Dati errati: numero di carte diverso da 3");
        this.tris = tris;
    }

    public String getGiocatore() {
        return giocatore;
    }

    public List<Integer> getTris() {
        return tris;
    }
}
