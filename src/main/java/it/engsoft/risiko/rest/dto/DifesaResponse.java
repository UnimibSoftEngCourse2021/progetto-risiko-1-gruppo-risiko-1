package it.engsoft.risiko.rest.dto;

import it.engsoft.risiko.data.model.Combattimento;

import java.util.List;

/**
 * Classe utilizzata per gestire i dati in entrata relativi ai continenti.
 */
public final class DifesaResponse {
    private final List<Integer> dadoAtt;
    private final List<Integer> dadoDif;
    private final int vittimeAtt;
    private final int vittimeDif;
    private final boolean obiettivoRaggiuntoAtt;
    private final boolean vittoriaAtt;
    private final boolean eliminato;

    /**
     * Crea un oggetto contenente il risultato del combattimento.
     * @param combattimento Il combattimento appena concluso
     * @param obiettivoRaggiuntoAtt booleano che indica se il giocatore attaccante ha raggiunto il suo obiettivo
     * @param difensoreEliminato booleano che indica se il giocatore difensore è stato eliminato
     */
    public DifesaResponse(Combattimento combattimento, boolean obiettivoRaggiuntoAtt, boolean difensoreEliminato) {
        this.dadoAtt = combattimento.getTiriAttaccante();
        this.dadoDif = combattimento.getTiriDifensore();
        this.vittimeAtt = combattimento.getVittimeAttaccante();
        this.vittimeDif = combattimento.getVittimeDifensore();
        this.vittoriaAtt = combattimento.getConquista();
        this.obiettivoRaggiuntoAtt = obiettivoRaggiuntoAtt;
        this.eliminato = difensoreEliminato;
    }

    /**
     * Restituisce il risultato del lancio del dado dell'attaccante.
     * @return il risultato del lancio del dado dell'attaccante
     */
    public List<Integer> getDadoAtt() {
        return dadoAtt;
    }

    /**
     *Restituisce il risultato del lancio del dado del difensore.
     * @return il risultato del lancio del dado del difensore
     */
    public List<Integer> getDadoDif() {
        return dadoDif;
    }

    /**
     * Restituisce il numero di armate perse dall'attaccante.
     * @return il numero di armate perse dall'attaccante
     */
    public int getVittimeAtt() {
        return vittimeAtt;
    }

    /**
     * Restituisce il numero di armate perse dal difensore.
     * @return il numero di armate perse dal difensore
     */
    public int getVittimeDif() {
        return vittimeDif;
    }

    /**
     * Restituisce true se l'attaccante ha conquistato il territorio attaccato, false altrimenti.
     * @return true se l'attaccante ha conquistato il territorio attaccato, false altrimenti
     */
    public boolean isVittoriaAtt() {
        return vittoriaAtt;
    }

    /**
     * Restituisce true se l'attaccante ha raggiunto il suo obiettivo, false altrimenti.
     * @return true se l'attaccante ha raggiunto il suo obiettivo, false altrimenti
     */
    public boolean isObiettivoRaggiuntoAtt() {
        return obiettivoRaggiuntoAtt;
    }

    /**
     * Restituisce true se il difensore è stato eliminato, false altrimenti.
     * @return true se il difensore è stato eliminato, false altrimenti
     */
    public boolean isEliminato() { return eliminato; }
}