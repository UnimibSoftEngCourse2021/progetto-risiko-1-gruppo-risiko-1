package it.engsoft.risiko.model;

import java.util.ArrayList;

public class Partita {
    public ArrayList<Giocatore> giocatori = new ArrayList<>();
    public Giocatore giocatoreAttivo;
    public Turno turno;
    public Mappa mappa;
    public Modalita modalita;

    public enum Modalita{
        VELOCE,
        NORMALE,
        LENTA
    };
}
