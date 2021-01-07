package it.engsoft.risiko.model;

import java.util.ArrayList;
import java.util.Objects;

public class Giocatore {
    private final ArrayList<Stato> stati = new ArrayList<Stato>();
    private String nome;
    private ArrayList<CartaTerritorio> carteTerritorio = new ArrayList<CartaTerritorio>();
    private Obiettivo obiettivo;
    private Giocatore uccisore;
    private int truppeDisponibili = 0;

    public Giocatore(String nome){
        this.nome = nome;
    }
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

    public void aggiungiStato(Stato stato) {
        if (stato == null)
            throw new RuntimeException("Stato non valido");

        stati.add(stato);
    }

    public void rimuoviStato(Stato stato) {
        if(stato == null || !stati.contains(stato))
            throw new RuntimeException("Stato non valido");

        stati.remove(stato);
    }

    public ArrayList<CartaTerritorio> getCarteTerritorio() {
        return carteTerritorio;
    }

    public void aggiungiCartaTerritorio(CartaTerritorio cartaTerritorio) {
        if (cartaTerritorio == null)
            throw new RuntimeException("Carta territorio non valida");

        carteTerritorio.add(cartaTerritorio);
    }

    public void rimuoviCartaTerritorio(CartaTerritorio cartaTerritorio) {
        if(cartaTerritorio == null || !carteTerritorio.contains(cartaTerritorio))
            throw new RuntimeException("Carta territorio non valida");

        carteTerritorio.remove(cartaTerritorio);
    }

    public Obiettivo getObiettivo() {
        return obiettivo;
    }

    public void setObiettivo(Obiettivo obiettivo) {
        if (obiettivo == null)
            throw new RuntimeException();

        this.obiettivo = obiettivo;
    }

    public Giocatore getUccisore() {
        return uccisore;
    }

    public void setUccisore(Giocatore uccisore) {
        if (uccisore == null)
            throw new RuntimeException();

        this.uccisore = uccisore;
    }

    public int getTruppeDisponibili() {
        return truppeDisponibili;
    }

    public void setTruppeDisponibili(int truppeDisponibili) {
        if(truppeDisponibili < 0)
            throw new RuntimeException("Truppe disponibili giocatore è negativo");
        this.truppeDisponibili = truppeDisponibili;
    }

    public void modificaTruppeDisponibili(int truppeDisponibili) {
        if(this.truppeDisponibili + truppeDisponibili < 0)
            throw new RuntimeException("Truppe disponibili giocatore è negativo");
        this.truppeDisponibili = this.truppeDisponibili + truppeDisponibili;
    }

    public boolean isEliminato() {
        return stati.size() == 0;
    }

    public boolean obRaggiunto() {
        return obiettivo.raggiunto(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Giocatore giocatore = (Giocatore) o;
        return nome.equals(giocatore.nome);
    }
}
