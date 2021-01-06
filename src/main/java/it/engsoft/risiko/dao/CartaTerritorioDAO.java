package it.engsoft.risiko.dao;

import it.engsoft.risiko.model.CartaTerritorio;

import java.util.UUID;

public class CartaTerritorioDAO {
    private int id;
    private String statoRappresentato;
    private String figura;

    public CartaTerritorioDAO(CartaTerritorio cartaTerritorio) {
        this.id = cartaTerritorio.getId();
        this.statoRappresentato = cartaTerritorio.getStatoRappresentato().getNome();
        this.figura = cartaTerritorio.getFigura().toString();
    }

    public String getStatoRappresentato() {
        return statoRappresentato;
    }

    public String getFigura() {
        return figura;
    }
}