package it.engsoft.risiko.http.dto;

import it.engsoft.risiko.exceptions.DatiErratiException;

/**
 * Classe utilizzata per gestire i dati in entrata relativi alla difesa di un territorio.
 */
public final class DifesaRequest {
    private final String giocatore;
    private final int armate;

    /**
     * Crea un oggetto contenente i dati della difesa inviati dall'utente.
     * @param giocatore Il nome del giocatore che subisce un attacco
     * @param armate Il numero di armate con cui il giocatore intende difendersi (tra 1 e 3)
     */
    public DifesaRequest(String giocatore, int armate) {
        if(giocatore == null || giocatore.trim().isEmpty())
            throw new DatiErratiException("Dati errati: giocatore nullo o vuoto");
        this.giocatore = giocatore;

        if(armate <= 0)
            throw new DatiErratiException("Dati errati: numero armate negativo");
        this.armate = armate;
    }

    /**
     * Restituisce il nome del giocatore difensore.
     * @return il nome del giocatore difensore
     */
    public String getGiocatore() {
        return giocatore;
    }

    /**
     * Restituisce il numero di armate con cui si difende il giocatore.
     * @return il numero di armate con cui si difende il giocatore
     */
    public int getArmate() {
        return armate;
    }
}
