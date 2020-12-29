package it.engsoft.risiko.model;

import java.util.ArrayList;

public class Partita {
    private ArrayList<Giocatore> giocatori = new ArrayList<>();
    private Giocatore giocatoreAttivo;
    private Turno turno;
    private Mappa mappa;
}
