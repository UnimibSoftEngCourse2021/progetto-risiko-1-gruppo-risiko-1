package it.engsoft.risiko.rest.DTO;

import it.engsoft.risiko.data.model.CartaTerritorio;

public final class CartaTerritorioDTO {
    private final int id;
    private final String statoRappresentato;
    private final String figura;

    public CartaTerritorioDTO(CartaTerritorio cartaTerritorio) {
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
