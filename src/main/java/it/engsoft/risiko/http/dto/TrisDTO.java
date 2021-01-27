package it.engsoft.risiko.http.dto;

import it.engsoft.risiko.exceptions.DatiErratiException;

import java.util.List;

/**
 * Classe utilizzata per gestire i dati in ingresso relativi ad un tris di carte territorio
 */
public final class TrisDTO {
    private final String giocatore;
    private final List<Integer> tris;

    /**
     * Crea un oggetto contenente i dati relativi ad un tris di carte territorio
     * @param giocatore il giocatore che gioca il tris
     * @param tris lista di interi contenente gli id delle carte giocate
     */
    public TrisDTO(String giocatore, List<Integer> tris) {
        if(giocatore == null || giocatore.trim().isEmpty())
            throw new DatiErratiException("Dati errati: giocatore nullo o vuoto");
        this.giocatore = giocatore;

        if(tris.size() != 3)
            throw new DatiErratiException("Dati errati: numero di carte diverso da 3");
        this.tris = tris;
    }

    /**
     * Restituisce il nome del giocatore che gioca il tris.
     * @return nome del giocatore
     */
    public String getGiocatore() {
        return giocatore;
    }

    /**
     * Restituisce la lista contenente gli id delle carte giocate
     * @return lista contenente gli id delle carte giocate
     */
    public List<Integer> getTris() {
        return tris;
    }
}
