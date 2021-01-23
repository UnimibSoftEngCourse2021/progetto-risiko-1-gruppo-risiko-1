package it.engsoft.risiko.rest.dao;

import it.engsoft.risiko.data.model.CartaTerritorio;

public class CartaTerritorioDAO {
    private int id;
    private String statoRappresentato;
    private String figura;

    public CartaTerritorioDAO(CartaTerritorio cartaTerritorio) {
        this.id = cartaTerritorio.getId();
        this.statoRappresentato = cartaTerritorio.getStatoRappresentato() == null ?
                "" : cartaTerritorio.getStatoRappresentato().getNome();
        this.figura = cartaTerritorio.getFigura().toString();
    }

    public int getId() { return id; }

    public String getStatoRappresentato() {
        return statoRappresentato;
    }

    public String getFigura() {
        return figura;
    }
}
