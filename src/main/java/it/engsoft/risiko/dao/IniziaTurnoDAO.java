package it.engsoft.risiko.dao;

import it.engsoft.risiko.model.Turno;

public class IniziaTurnoDAO {
    private int numeroTurno;
    private String giocatore;
    private int armateStati;
    private int armateContinenti;

    public IniziaTurnoDAO(Turno turno, int armateStati, int armateContinenti) {
        this.numeroTurno = turno.getNumero();
        this.giocatore = turno.getGiocatoreAttivo().getNome();
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
