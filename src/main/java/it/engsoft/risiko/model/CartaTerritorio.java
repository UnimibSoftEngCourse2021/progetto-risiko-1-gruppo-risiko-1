package it.engsoft.risiko.model;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartaTerritorio that = (CartaTerritorio) o;
        return Objects.equals(statoRappresentato, that.statoRappresentato) && figura == that.figura;
    }
}