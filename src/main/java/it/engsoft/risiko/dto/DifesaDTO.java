package it.engsoft.risiko.dto;

import it.engsoft.risiko.exceptions.DatiErratiException;

public final class DifesaDTO {
    private final String giocatore;
    private final int armate;

    public DifesaDTO(String giocatore, int armate) {
        if(giocatore == null || giocatore.trim().isEmpty())
            throw new DatiErratiException("Dati errati: giocatore nullo o vuoto");
        this.giocatore = giocatore;

        if(armate <= 0)
            throw new DatiErratiException("Dati errati: numero armate negativo");
        this.armate = armate;
    }

    public String getGiocatore() {
        return giocatore;
    }

    public int getArmate() {
        return armate;
    }
}
