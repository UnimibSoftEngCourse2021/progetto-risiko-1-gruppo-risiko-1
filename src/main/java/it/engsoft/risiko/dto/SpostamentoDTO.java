package it.engsoft.risiko.dto;

import it.engsoft.risiko.exceptions.DatiErratiException;

public final class SpostamentoDTO {
    private final String giocatore;
    private final long statoPartenza;
    private final long statoArrivo;
    private final int armate;

    public SpostamentoDTO(String giocatore, Long statoPartenza, Long statoArrivo, int armate) {
        if(giocatore == null || giocatore.trim().isEmpty())
            throw new DatiErratiException();
        this.giocatore = giocatore;

        if(statoPartenza < 0L)
            throw new DatiErratiException();
        this.statoPartenza = statoPartenza;

        if(statoArrivo < 0L)
            throw new DatiErratiException();
        this.statoArrivo = statoArrivo;

        if(armate <= 0)
            throw new DatiErratiException();
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
