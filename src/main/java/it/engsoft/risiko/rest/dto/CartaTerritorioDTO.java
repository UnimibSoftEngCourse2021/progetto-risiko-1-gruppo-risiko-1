package it.engsoft.risiko.rest.dto;

import it.engsoft.risiko.data.model.CartaTerritorio;

/**
 * Classe utilizzata per gestire i dati in uscita relativi ad una carta territorio.
 */
public final class CartaTerritorioDTO {
    private final int id;
    private final String statoRappresentato;
    private final String figura;

    /**
     * Crea un oggetto contenente i dati più significativi di una carta territorio.
     * @param cartaTerritorio La carta territorio da cui prendere i dati.
     */
    public CartaTerritorioDTO(CartaTerritorio cartaTerritorio) {
        this.id = cartaTerritorio.getId();
        this.statoRappresentato = cartaTerritorio.getStatoRappresentato() == null ?
                "" : cartaTerritorio.getStatoRappresentato().getNome();
        this.figura = cartaTerritorio.getFigura().toString();
    }

    /**
     * Restituisce l'id della carta territorio rappresentata.
     * @return l'id della carta territorio rappresentata
     */
    public int getId() {
        return id;
    }

    /**
     * Restituisce il nome dello stato rappresentato dalla carta.
     * @return il nome dello stato rappresentato dalla carta
     */
    public String getStatoRappresentato() {
        return statoRappresentato;
    }

    /**
     * Restituisce la figura presente sulla carta.
     * @return la figura presente sulla carta
     */
    public String getFigura() {
        return figura;
    }
}
