package it.engsoft.risiko.rest.dto;

import it.engsoft.risiko.exceptions.DatiErratiException;

public final class SpostamentoDTO {
    private final String giocatore;
    private final long statoPartenza;
    private final long statoArrivo;
    private final int armate;

    public SpostamentoDTO(String giocatore, Long statoPartenza, Long statoArrivo, int armate) {
        if(giocatore == null || giocatore.trim().isEmpty())
            throw new DatiErratiException("Dati errati: giocatore nullo o vuoto");
        this.giocatore = giocatore;

        if(statoPartenza < 0L)
            throw new DatiErratiException("Dati errati: id stato partenza non valido");
        this.statoPartenza = statoPartenza;

        if(statoArrivo < 0L)
            throw new DatiErratiException("Dati errati: id stato arrivo on valido");
        this.statoArrivo = statoArrivo;

        if(armate <= 0)
            throw new DatiErratiException("Dati errati: numero armate zero o negativo");
        this.armate = armate;
    }

    public String getGiocatore() {
        return giocatore;
    }

    public long getStatoPartenza() {
        return statoPartenza;
    }

    public long getStatoArrivo() {
        return statoArrivo;
    }

    public int getArmate() {
        return armate;
    }
}
