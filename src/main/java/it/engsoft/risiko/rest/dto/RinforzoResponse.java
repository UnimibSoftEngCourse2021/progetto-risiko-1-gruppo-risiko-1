package it.engsoft.risiko.rest.dto;

public final class RinforzoResponse {
    private final String giocatore;
    private final boolean preparazione;
    private final boolean vittoria;

    public RinforzoResponse(String giocatore, boolean preparazione, boolean vittoria) {
        this.giocatore = giocatore;
        this.preparazione = preparazione;
        this.vittoria = vittoria;
    }

    public String getGiocatore() {
        return giocatore;
    }

    public boolean isPreparazione() {
        return preparazione;
    }

    public boolean isVittoria() {
        return vittoria;
    }
}
