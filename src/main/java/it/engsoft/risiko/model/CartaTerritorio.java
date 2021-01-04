package it.engsoft.risiko.model;

public class CartaTerritorio {
    private final Stato statoRappresentato;
    private Figura figura;

    public enum Figura {
        CANNONE,
        FANTE,
        CAVALLO,
        JOLLY
    };

    public CartaTerritorio(Stato statoRappresentato) {
        this.statoRappresentato = statoRappresentato;
    }

    // stato rappresentato
    public Stato getStatoRappresentato() {
        return statoRappresentato;
    }

    // figura
    public Figura getFigura() {
        return figura;
    }

    public void setFigura(Figura figura) {
        if (figura == null)
            throw new RuntimeException("Figura nulla");
        this.figura = figura;
    }
}