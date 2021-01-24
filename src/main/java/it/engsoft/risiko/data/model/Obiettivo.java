package it.engsoft.risiko.data.model;

/**
 * Rrappresenta un generico obiettivo che un giocatore deve raggiungere per vincere la partita.
 */
public interface Obiettivo {
    /**
     * Fornisce una descrizione testuale dell'obiettivo, da mostrare all'utente.
     * @return la descrizione testuale
     */
    String getDescrizione();

    /**
     * Verifica che l'obiettivo sia stato raggiunto, qualora assegnato al giocatore ricevuto come parametro.
     * @param giocatore il giocatore di cui si vuole verificare il raggiungimento dell'obiettivo
     * @return true se l'obiettivo è raggiunto
     */
    boolean raggiunto(Giocatore giocatore);
}
