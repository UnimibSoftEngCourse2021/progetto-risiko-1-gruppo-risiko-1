package it.engsoft.risiko.model;

import java.util.List;

public class ConqContinenti extends Obiettivo {
    private Mappa mappa;
    private final List<Continente> targetContinenti;
    private final int continentiExtra;

    public ConqContinenti(List<Continente> targetContinenti) {
        this(targetContinenti, 0);
    }

    public ConqContinenti(List<Continente> targetContinenti, int continentiExtra) {
        this.targetContinenti = targetContinenti;
        this.continentiExtra = continentiExtra;
    }

    public List<Continente> getTargetContinenti() {
        return targetContinenti;
    }

    public int getContinentiExtra() {
        return continentiExtra;
    }

    public void setMappa(Mappa mappa) {
        if(mappa == null)
            throw new RuntimeException("Mappa non valida");

        this.mappa = mappa;
    }

    public boolean raggiunto(Giocatore giocatore) {
        if (giocatore == null)
            throw new RuntimeException("Giocatore non valido");

        for (Continente continente : targetContinenti) {
            if (continente.getProprietario().equals(giocatore))
                return false;
        }

        return checkExtra(giocatore);
    }

    /**
     * @param giocatore Il giocatore che ha l'obiettivo ConqContinenti.
     * @return true se il giocatore ha conquistato, oltre ai continenti target,
     * un numero di continenti pari a continentiExtra
     */
    public boolean checkExtra(Giocatore giocatore) {
        if (continentiExtra == 0)
            return true;

        int count = 0;

        for (Continente continente : mappa.getContinenti()) {
            if (!targetContinenti.contains(continente) && continente.getProprietario().equals(giocatore)) {
                count++;
            }
        }

        return count == continentiExtra;
    }
}
