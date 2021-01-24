package it.engsoft.risiko.data.model;

import it.engsoft.risiko.exceptions.ModelDataException;

import java.util.Objects;

/**
 * Rappresenta una carta territorio: è definita da un ID univoco (assegnato arbitrariamente dall'applicazione a puro
 * scopo di riconoscimento), uno stato rappresentato e una figura.
 */
public class CartaTerritorio {
    private final int id;
    private final Stato statoRappresentato;
    private final Figura figura;

    /** Le quattro figure che possono comparire su una carta territorio */
    public enum Figura {
        CANNONE,
        FANTE,
        CAVALLO,
        JOLLY
    }

    /**
     * Creare una carta territorio specificandone i parametri caratteristici.
     * @param id l'id della carta da creare
     * @param statoRappresentato lo stato rappresentato dalla carta da creare
     * @param figura la figura rappresentata dalla carta da creare
     */
    public CartaTerritorio(int id, Stato statoRappresentato, Figura figura) {
        if (id < 0)
            throw new ModelDataException("ID carta territorio non valido.");

        if (figura == null)
            throw new ModelDataException("Figura carta territorio nulla");

        if ((statoRappresentato == null && !figura.equals(Figura.JOLLY)) ||
                (statoRappresentato != null && figura.equals(Figura.JOLLY)))
            throw new ModelDataException("Figura non valida");

        this.id = id;
        this.statoRappresentato = statoRappresentato;
        this.figura = figura;
    }

    /**
     * Restituisce l'ID della carta.
     * @return l'ID della carta
     */
    public int getId() {
        return id;
    }

    /**
     * Restituisce lo stato rappresentato dalla carta.
     * @return lo stato rappresentato dalla carta
     */
    public Stato getStatoRappresentato() {
        return statoRappresentato;
    }

    /**
     * Restituisce la figura rappresentata dalla carta.
     * @return la figura rappresentata dalla carta
     */
    public Figura getFigura() {
        return figura;
    }

    /**
     * Confronta questa carta territorio con un altro oggetto e stabilisce se essi rappresentano la medesima carta.
     * @param o l'oggetto da confrontare
     * @return true se questa carta territorio e l'oggetto ricevuto come parametro rappresentano la stessa carta
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartaTerritorio that = (CartaTerritorio) o;
        return id == that.id;
    }

    /**
     * Calcola l'hashcode di quest'oggetto.
     * @return l'hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}