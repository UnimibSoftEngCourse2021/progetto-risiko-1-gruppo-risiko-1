package it.engsoft.risiko.dao;

import it.engsoft.risiko.model.CartaTerritorio;

public class CartaTerritorioDAO {
    private String statoRappresentato;
    private String figura;

    public CartaTerritorioDAO(CartaTerritorio cartaTerritorio) {
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
