package it.engsoft.risiko.model;

import java.util.ArrayList;
import java.util.Objects;

public class Giocatore {
    private String nome;
    private final ArrayList<Stato> stati = new ArrayList<Stato>();
    private ArrayList<CartaTerritorio> carteTerritorio = new ArrayList<CartaTerritorio>();
    private Obiettivo obiettivo;
    private Giocatore uccisore;


    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty())
            throw new RuntimeException();

        this.nome = nome;
    }

    public ArrayList<Stato> getStati() {
        return stati;
    }

//    public void setStati(ArrayList<Stato> stati) {
//        if (stati == null)
//            throw new RuntimeException();
//
//        this.stati = stati;
//    }

    public void aggiungiStato(Stato stato) {
        stati.add(stato);
    }

    public void rimuoviStato(Stato stato) {
        boolean ris = stati.remove(stato);
//        if (!ris)
//            throw new RuntimeException("Non c'era lo stato");
    }

    public ArrayList<CartaTerritorio> getCarteTerritorio() {
        return carteTerritorio;
    }

    public void setCarteTerritorio(ArrayList<CartaTerritorio> carteTerritorio) {
        if (carteTerritorio == null)
            throw new RuntimeException();

        this.carteTerritorio = carteTerritorio;
    }

    // TODO: metodi aggiungi/rimuovi sulle carte territorio anziche set su tutto l'array

    public Obiettivo getObiettivo() {
        return obiettivo;
    }

    public void setObiettivo(Obiettivo obiettivo) {
        if (obiettivo == null)
            throw new RuntimeException();

        this.obiettivo = obiettivo;
    }

    public boolean isEliminato() {
        return stati.size() == 0;
    }

    public Giocatore getUccisore() {
        return uccisore;
    }

    public void setUccisore(Giocatore uccisore) {
        if (uccisore == null)
            throw new RuntimeException();

        this.uccisore = uccisore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Giocatore giocatore = (Giocatore) o;
        return nome.equals(giocatore.nome);
    }
}
