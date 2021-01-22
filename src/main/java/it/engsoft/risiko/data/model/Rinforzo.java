package it.engsoft.risiko.data.model;

import it.engsoft.risiko.exceptions.ModelDataException;

public class Rinforzo {
    private final int numeroArmate;
    private final Stato target;
    private boolean eseguito;

    public Rinforzo(final Stato target, final int numeroArmate) {
        if (target == null || numeroArmate < 1) {
            throw new ModelDataException("Invalid constructor parameters (Rinforzo)");
        }
        this.target = target;
        this.numeroArmate = numeroArmate;
        this.eseguito = false;
    }

    public void esegui() {
        if (eseguito) {
            throw new ModelDataException("Il rinforzo è già stato eseguito");
        }

        target.aggiungiArmate(numeroArmate);
        this.eseguito = true;
    }
}
