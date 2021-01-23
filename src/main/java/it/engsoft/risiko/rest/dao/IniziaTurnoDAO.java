package it.engsoft.risiko.rest.dao;

public class IniziaTurnoDAO {
    private final String giocatore;
    private final int armateStati;
    private final int armateContinenti;

    public IniziaTurnoDAO(String giocatore, int armateStati, int armateContinenti) {
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
