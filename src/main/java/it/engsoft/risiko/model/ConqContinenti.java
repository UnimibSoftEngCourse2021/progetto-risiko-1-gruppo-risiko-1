package it.engsoft.risiko.model;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Un obiettivo che impone al giocatore di conquistare un certo numero di continenti, specificati o meno.
 */
public class ConqContinenti extends Obiettivo {
    private final Mappa mappa;
    private final List<Continente> targetContinenti;
    private final int continentiExtra;

    /**
     * Crea un nuovo obiettivo di conquista continenti.
     * @param targetContinenti l'elenco dei continenti da conquistare
     * @param continentiExtra il numero di continente "extra" a scelta da conquistare
     * @param mappa la mappa dove si svolge la partita
     */
    public ConqContinenti(List<Continente> targetContinenti, int continentiExtra, Mappa mappa) {
        this.targetContinenti = targetContinenti;
        this.continentiExtra = continentiExtra;
        this.mappa = mappa;
    }

    @Override
    public String getDescrizione() {
        List<String> nomiContinenti = targetContinenti.stream().map(Continente::getNome).collect(Collectors.toList());
        String desc = "Devi conquistare la totalità di " +
                nomiContinenti.get(0) + " e " + nomiContinenti.get(1);
        if (continentiExtra > 0)
            desc = desc + " più " + continentiExtra + " continenti a tua scelta";
        return desc;
    }

    /**
     * Ritorna l'elenco di continenti da conquistare.
     * @return l'elenco dei continenti
     */
    public List<Continente> getTargetContinenti() {
        return targetContinenti;
    }

    /**
     * Ritorna il numero di continenti extra da conquistare.
     * @return il numero di continenti extra
     */
    public int getContinentiExtra() {
        return continentiExtra;
    }

    @Override
    public boolean raggiunto(Giocatore giocatore) {
        if (giocatore == null)
            throw new RuntimeException("Giocatore non valido");

        for (Continente continente : targetContinenti) {
            if (!giocatore.equals(continente.getProprietario()))
                return false;
        }

        return checkExtra(giocatore);
    }

    /**
     * Verifica che il giocatore abbia conquistato i continenti extra a scelta richiesti.
     * @param giocatore Il giocatore che ha l'obiettivo ConqContinenti.
     * @return true se il giocatore ha conquistato, oltre ai continenti target,
     * un numero di continenti pari a continentiExtra
     */
    private boolean checkExtra(Giocatore giocatore) {
        if (continentiExtra == 0)
            return true;

        int count = 0;

        for (Continente continente : mappa.getContinenti()) {
            if (!targetContinenti.contains(continente) && giocatore.equals(continente.getProprietario())) {
                count++;
            }
        }

        return count == continentiExtra;
    }
}
