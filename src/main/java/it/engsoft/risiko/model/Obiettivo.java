package it.engsoft.risiko.model;

/**
 * Un'istanza di questa classe rappresenta un generico obiettivo che un giocatore deve raggiungere per vincere la
 * partita.
 */
public abstract class Obiettivo {
    /**
     * Fornisce una descrizione testuale dell'obiettivo, da mostrare all'utente.
     * @return la descrizione testuale
     */
    public abstract String getDescrizione();

    /**
     * Verifica che l'obiettivo sia stato raggiunto, qualora assegnato al giocatore ricevuto come parametro.
     * @param giocatore il giocatore di cui si vuole verificare il raggiungimento dell'obiettivo
     * @return true se l'obiettivo è raggiunto
     */
    public abstract boolean raggiunto(Giocatore giocatore);
}
