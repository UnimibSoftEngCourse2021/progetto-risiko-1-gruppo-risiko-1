package it.engsoft.risiko.rest.dto;

import it.engsoft.risiko.data.model.Giocatore;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class GiocatoreDTO {
    private final List<Long> idStati = new ArrayList<>();
    private final String nome;
    private final List<CartaTerritorioDTO> carteTerritorio;
    private final String obiettivo;
    private final String uccisore;
    private final int truppeDisponibili;

    public GiocatoreDTO(Giocatore giocatore) {
        giocatore.getStati().forEach(stato ->
                this.idStati.add(stato.getId()));
        this.nome = giocatore.getNome();
        this.carteTerritorio = giocatore.getCarteTerritorio().stream()
                .map(CartaTerritorioDTO::new)
                .collect(Collectors.toList());
        this.obiettivo = giocatore.getObiettivo().getDescrizione();
        this.uccisore = giocatore.getUccisore() == null ? null : giocatore.getUccisore().getNome();
        this.truppeDisponibili = giocatore.getArmateDisponibili();
    }

    public List<Long> getIdStati() {
        return idStati;
    }

    public String getNome() {
        return nome;
    }

    public List<CartaTerritorioDTO> getCarteTerritorio() {
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