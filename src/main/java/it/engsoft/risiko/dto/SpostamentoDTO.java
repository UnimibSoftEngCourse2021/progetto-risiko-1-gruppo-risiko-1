package it.engsoft.risiko.dto;

public final class SpostamentoDTO {
    private final String giocatore;
    private final long statoPartenza;
    private final long statoArrivo;
    private final int armate;

    public SpostamentoDTO(String giocatore, Long statoPartenza, Long statoArrivo, int armate) {
        if(giocatore == null || giocatore.trim().isEmpty())
            throw new RuntimeException("Nome giocatore non valido.");
        this.giocatore = giocatore;

        if(statoPartenza < 0L)
            throw new RuntimeException("Stato partenza non valido.");
        this.statoPartenza = statoPartenza;

        if(statoArrivo < 0L)
            throw new RuntimeException("Stato arrivo non valido.");
        this.statoArrivo = statoArrivo;

        if(armate <= 0)
            throw new RuntimeException("Numero armate non valido.");
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
