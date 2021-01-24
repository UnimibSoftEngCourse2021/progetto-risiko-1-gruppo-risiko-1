package it.engsoft.risiko.data.model;

import it.engsoft.risiko.exceptions.ModelDataException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Giocatore {
    private final List<Stato> stati = new ArrayList<>();
    private String nome;
    private final List<CartaTerritorio> carteTerritorio = new ArrayList<>();
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

    public List<Stato> getStati() {
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

    public List<CartaTerritorio> getCarteTerritorio() {
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
        if (!stati.isEmpty())
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

    public boolean isEliminato() { return stati.isEmpty(); }

    public boolean obRaggiunto() {
        return obiettivo.raggiunto(this);
    }

    public void consegnaCarteTerritorio(Giocatore giocatore) {
        if (giocatore == null)
            throw new ModelDataException("Giocatore null");

        giocatore.getCarteTerritorio().addAll(carteTerritorio);
        carteTerritorio.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Giocatore giocatore = (Giocatore) o;
        return nome.equals(giocatore.nome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome);
    }
}
