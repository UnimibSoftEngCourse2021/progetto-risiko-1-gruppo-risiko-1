package it.engsoft.risiko.dao;

import it.engsoft.risiko.model.Partita;

public class IniziaTurnoDAO {
    private int numeroTurno;
    private String giocatore;
    private int armateStati;
    private int armateContinenti = 0;
    private int armateTotali;

    public IniziaTurnoDAO(Partita partita) {
        this.numeroTurno = partita.getTurno().getNumero();
        this.giocatore = partita.getTurno().getGiocatoreAttivo().getNome();
        this.armateStati = partita.getTurno().getGiocatoreAttivo().getStati().size()/3;

        for (int i=0; i< partita.getGiocatoreAttivo().getStati().size(); i++) {
            if (partita.getGiocatoreAttivo().equals(partita.getMappa().getContinenti().get(i).getProprietario()))
                this.armateContinenti = partita.getMappa().getContinenti().get(i).getArmateBonus();
        }

        this.armateTotali = partita.getTurno().getGiocatoreAttivo().getTruppeDisponibili();
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
