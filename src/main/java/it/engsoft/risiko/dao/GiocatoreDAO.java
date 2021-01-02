package it.engsoft.risiko.dao;

import it.engsoft.risiko.model.Giocatore;

import java.util.List;
import java.util.stream.Collectors;

public class GiocatoreDAO {
    private final List<StatoDAO> stati;
    private String nome;
    private List<CartaTerritorioDAO> carteTerritorio;
    private String obiettivo;
    private String uccisore;
    private int truppeDisponibili;

    public GiocatoreDAO(Giocatore giocatore) {
        this.stati = giocatore.getStati().stream()
                .map(StatoDAO::new)
                .collect(Collectors.toList());
        this.nome = giocatore.getNome();
        this.carteTerritorio = giocatore.getCarteTerritorio().stream()
                .map(CartaTerritorioDAO::new)
                .collect(Collectors.toList());
        this.obiettivo = giocatore.getObiettivo().getDescrizione();
        this.uccisore = giocatore.getUccisore().getNome();
        this.truppeDisponibili = giocatore.getTruppeDisponibili();
    }

    public List<StatoDAO> getStati() {
        return stati;
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