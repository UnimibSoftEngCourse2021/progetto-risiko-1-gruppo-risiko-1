package it.engsoft.risiko.dao;

import it.engsoft.risiko.model.Giocatore;
import it.engsoft.risiko.model.Turno;

public class IniziaTurnoDAO {
    private int numeroTurno;
    private String giocatore;
    private int armateStati;
    private int armateContinenti;

    public IniziaTurnoDAO(int numeroTurno, String giocatore, int armateStati, int armateContinenti) {
        this.numeroTurno = numeroTurno;
        this.giocatore = giocatore;
        this.armateStati = armateStati;
        this.armateContinenti = armateContinenti;
    }

    public int getNumeroTurno() {
        return numeroTurno;
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
