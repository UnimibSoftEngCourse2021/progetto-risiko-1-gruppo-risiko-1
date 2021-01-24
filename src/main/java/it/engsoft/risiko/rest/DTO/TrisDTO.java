package it.engsoft.risiko.rest.DTO;

import it.engsoft.risiko.exceptions.DatiErratiException;

import java.util.List;

public final class TrisDTO {
    private final String giocatore;
    private final List<Integer> tris;

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
