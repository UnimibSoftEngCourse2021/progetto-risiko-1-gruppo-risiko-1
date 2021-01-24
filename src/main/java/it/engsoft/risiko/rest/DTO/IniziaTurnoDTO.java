package it.engsoft.risiko.rest.DTO;

public final class IniziaTurnoDTO {
    private final String giocatore;
    private final int armateStati;
    private final int armateContinenti;

    public IniziaTurnoDTO(String giocatore, int armateStati, int armateContinenti) {
        this.giocatore = giocatore;
        this.armateStati = armateStati;
        this.armateContinenti = armateContinenti;
    }

    public String getGiocatore() {
        return giocatore;
    }

    public int getArmateStati() {
        return armateStati;
    }

    public int getArmateContinenti() {
        return armateContinenti;
    }
}
