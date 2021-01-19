package it.engsoft.risiko.model;

import it.engsoft.risiko.exceptions.ModelDataException;

import java.util.Objects;

public class CartaTerritorio {
    private final int id;
    private final Stato statoRappresentato;
    private Figura figura;

    public enum Figura {
        CANNONE,
        FANTE,
        CAVALLO,
        JOLLY
    }

    public CartaTerritorio(int id, Stato statoRappresentato, Figura figura) {
        if(id < 0)
            throw new ModelDataException("ID carta territorio non valido.");

        this.id = id;
        this.statoRappresentato = statoRappresentato;
        this.figura = figura;

        if (figura == null)
            throw new ModelDataException("Figura carta territorio nulla");

        if ((statoRappresentato == null && !figura.equals(Figura.JOLLY)) ||
                (statoRappresentato != null && figura.equals(Figura.JOLLY)))
            throw new ModelDataException("Figura non valida");
    }

    public int getId() {
        return id;
    }

    // stato rappresentato
    public Stato getStatoRappresentato() {
        return statoRappresentato;
    }

    // figura
    public Figura getFigura() {
        return figura;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartaTerritorio that = (CartaTerritorio) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}