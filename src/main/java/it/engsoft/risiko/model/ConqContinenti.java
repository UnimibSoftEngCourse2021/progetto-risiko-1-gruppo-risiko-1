package it.engsoft.risiko.model;

import java.util.ArrayList;

public class ConqContinenti extends Obiettivo {
    private Mappa mappa;
    private ArrayList<Continente> targetContinenti;
    private int continentiExtra = 0;

    public ConqContinenti() {
    }

    public ArrayList<Continente> getTargetContinenti() {
        return targetContinenti;
    }

    public void setTargetContinenti(ArrayList<Continente> targetContinenti) {
        if (targetContinenti == null)
            throw new RuntimeException("Parametro non valido (setContinenti).");

        this.targetContinenti = targetContinenti;
    }

    public int getContinentiExtra() {
        return continentiExtra;
    }

    public void setContinentiExtra(int continentiExtra) {
        if (continentiExtra < 0)
            throw new RuntimeException("Numero continenti extra non valido.");

        this.continentiExtra = continentiExtra;
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
