package it.engsoft.risiko.rest.dao;

import it.engsoft.risiko.data.model.Giocatore;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GiocatoreDAO {
    private List<Long> idStati = new ArrayList<>();
    private String nome;
    private List<CartaTerritorioDAO> carteTerritorio;
    private String obiettivo;
    private String uccisore;
    private int truppeDisponibili;

    public GiocatoreDAO(Giocatore giocatore) {
        giocatore.getStati().forEach(stato ->
                this.idStati.add(stato.getId()));
        this.nome = giocatore.getNome();
        this.carteTerritorio = giocatore.getCarteTerritorio().stream()
                .map(CartaTerritorioDAO::new)
                .collect(Collectors.toList());
        this.obiettivo = giocatore.getObiettivo().getDescrizione();
        this.uccisore = giocatore.getUccisore() == null ? null : giocatore.getUccisore().getNome();
        this.truppeDisponibili = giocatore.getTruppeDisponibili();
    }

    public List<Long> getIdStati() {
        return idStati;
    }

    public String getNome() {
        return nome;
    }

    public List<CartaTerritorioDAO> getCarteTerritorio() {
        return carteTerritorio;
    }

    public String getObiettivo() {
        return obiettivo;
    }

    public String getUccisore() {
        return uccisore;
    }

    public int getTruppeDisponibili() {
        return truppeDisponibili;
    }
}