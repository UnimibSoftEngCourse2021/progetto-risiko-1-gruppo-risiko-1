package it.engsoft.risiko.model;

import java.util.ArrayList;

public class Partita {
    private final ArrayList<Giocatore> giocatori = new ArrayList<>();
    private Giocatore giocatoreAttivo;
    private Turno turno;
    private Mappa mappa;

    public void setGiocatori(Giocatore giocatori) {
        this.giocatori = giocatori;
    }
}
