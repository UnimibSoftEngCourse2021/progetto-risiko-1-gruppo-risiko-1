package it.engsoft.risiko.model;

import java.util.ArrayList;
import java.util.List;

public class Partita {
    public List<Giocatore> giocatori = new ArrayList<>();
    public Giocatore giocatoreAttivo;
    public Turno turno;
    public Mappa mappa;
    public Modalita modalita;

    public enum Modalita{
        VELOCE,
        NORMALE,
        LENTA
    };

    public List<Giocatore> getGiocatori() {
        return giocatori;
    }

    public void setGiocatori(List<Giocatore> giocatori) {
        this.giocatori = giocatori;
    }

    public Giocatore getGiocatoreAttivo() {
        return giocatoreAttivo;
    }

    public void setGiocatoreAttivo(Giocatore giocatoreAttivo) {
        this.giocatoreAttivo = giocatoreAttivo;
    }

    public Turno getTurno() {
        return turno;
    }

    public void setTurno(Turno turno) {
        this.turno = turno;
    }

    public Mappa getMappa() {
        return mappa;
    }

    public void setMappa(Mappa mappa) {
        this.mappa = mappa;
    }

    public Modalita getModalita() {
        return modalita;
    }

    public void setModalita(Modalita modalita) {
        this.modalita = modalita;
    }
}
