package it.engsoft.risiko.data.model;

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
        this.setNome(nome);
    }
    public String getNome() {
        return nome;
    }

    private void setNome(String nome) {
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
        if(stati.contains(stato))
            throw new ModelDataException("Si è cercato di aggiungere al giocatore " + nome + " uno stato già presente");

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

    public void rimuoviCartaTerritorio(CartaTerritorio cartaTerritorio) {
        if(cartaTerritorio != null)
            carteTerritorio.remove(cartaTerritorio);
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

    public void setUccisore(Giocatore uccisore) {
        if (uccisore == null)
            throw new ModelDataException("Uccisore è null");
        if (equals(uccisore))
            throw new ModelDataException("Un giocatore non può sconfiggere se stesso");
        if (stati.size() > 0)
            throw new ModelDataException("Il giocatore su cui è chiamato setUccisore non è ancora eliminato");

        this.uccisore = uccisore;
    }

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
