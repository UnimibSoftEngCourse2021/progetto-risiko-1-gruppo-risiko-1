package it.engsoft.risiko.dao;

import it.engsoft.risiko.model.Turno;

public class IniziaTurnoDAO {
    private int numeroTurno;
    private String giocatore;
    private int armateStati;
    private int armateContinenti;
    private int armateTotali;

    // TODO: sistemare numero armate stati, impossibile(?) accedere ad armate continente
    public IniziaTurnoDAO(Turno turno) {
        this.numeroTurno = turno.getNumero();
        this.giocatore = turno.getGiocatoreAttivo().getNome();
        this.armateStati = turno.getGiocatoreAttivo().getStati().size()/3;

        //this.armateContinenti = turno.getGiocatoreAttivo().getStati().;

        this.armateTotali = turno.getGiocatoreAttivo().getTruppeDisponibili();
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

    public int getArmateTotali() { return armateTotali; }
}
