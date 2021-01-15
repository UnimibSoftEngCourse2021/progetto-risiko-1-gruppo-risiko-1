package it.engsoft.risiko.model;

import it.engsoft.risiko.exceptions.ModelDataException;

import java.util.ArrayList;

public class Giocatore {
    private final ArrayList<Stato> stati = new ArrayList<>();
    private String nome;
    private final ArrayList<CartaTerritorio> carteTerritorio = new ArrayList<>();
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
            throw new ModelDataException("Nome in Giocatore.setNome non valido");

        this.nome = nome;
    }

    public ArrayList<Stato> getStati() {
        return stati;
    }

    public void aggiungiStato(Stato stato) {
        if (stato == null)
            throw new ModelDataException("Stato in Giocatore.aggiungiStato non valido");

        stati.add(stato);
    }

    public void rimuoviStato(Stato stato) {
        if(stato == null || !stati.contains(stato))
            throw new ModelDataException("Stato in Giocatore.rimuoviStato non valido");

        stati.remove(stato);
    }

    public ArrayList<CartaTerritorio> getCarteTerritorio() {
        return carteTerritorio;
    }

    public void aggiungiCartaTerritorio(CartaTerritorio cartaTerritorio) {
        if (cartaTerritorio == null)
            throw new ModelDataException("Carta territorio in Giocatore.aggiungiCartaTerritorio nulla");

        carteTerritorio.add(cartaTerritorio);
    }

    public Obiettivo getObiettivo() {
        return obiettivo;
    }

    public void setObiettivo(Obiettivo obiettivo) {
        if (obiettivo == null)
            throw new ModelDataException("Obiettivo in Giocatore.setObiettivo nullo");

        this.obiettivo = obiettivo;
    }

    public Giocatore getUccisore() {
        return uccisore;
    }

    public void setUccisore(Giocatore uccisore) { this.uccisore = uccisore; }

    public int getTruppeDisponibili() {
        return truppeDisponibili;
    }

    public void setTruppeDisponibili(int truppeDisponibili) {
        if(truppeDisponibili < 0)
            throw new ModelDataException("Truppe disponibili in Giocatore.setTruppeDisponibili è negativo");
        this.truppeDisponibili = truppeDisponibili;
    }

    public void modificaTruppeDisponibili(int truppeDisponibili) {
        if(this.truppeDisponibili + truppeDisponibili < 0)
            throw new ModelDataException("Truppe disponibili in Giocatore.modificaTruppeDisponibili è negativo");
        this.truppeDisponibili = this.truppeDisponibili + truppeDisponibili;
    }

    public boolean isEliminato() { return stati.size() == 0; }

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
