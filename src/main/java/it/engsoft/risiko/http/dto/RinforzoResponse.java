package it.engsoft.risiko.http.dto;

/**
 * Classe utilizzata per gestire i dati in uscita relativi ad un rinforzo.
 */
public final class RinforzoResponse {
    private final String giocatore;
    private final boolean preparazione;
    private final boolean vittoria;

    /**
     * Crea un oggetto contenente i dati relativi ad un rinforzo.
     * @param giocatore il giocatore che ha eseguito il rinforzo
     * @param preparazione booleano che indica se la partita è in fase di preparazione
     * @param vittoria booleano che indica se il giocatore ha raggiunto il suo obiettivo
     */
    public RinforzoResponse(String giocatore, boolean preparazione, boolean vittoria) {
        this.giocatore = giocatore;
        this.preparazione = preparazione;
        this.vittoria = vittoria;
    }

    /**
     * Restituisce il nome del giocatore che ha eseguito il rinforzo.
     * @return il nome del giocatore
     */
    public String getGiocatore() {
        return giocatore;
    }

    /**
     * Restituisce true se la partita è in fase di preparazione, false altrimenti.
     * @return true se la partita è in fase di preparazione, false altrimenti
     */
    public boolean isPreparazione() {
        return preparazione;
    }

    /**
     * Restituisce true se il giocatore ha raggiunto il suo obiettivo, false altrimenti.
     * @return true se il giocatore ha raggiunto il suo obiettivo, false altrimenti
     */
    public boolean isVittoria() {
        return vittoria;
    }
}
