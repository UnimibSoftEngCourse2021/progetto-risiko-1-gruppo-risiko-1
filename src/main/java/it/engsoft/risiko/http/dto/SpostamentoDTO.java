package it.engsoft.risiko.http.dto;

import it.engsoft.risiko.exceptions.DatiErratiException;

/**
 * Classe utilizzata per gestire i dati in uscita relativi ad uno spostamento.
 */
public final class SpostamentoDTO {
    private final String giocatore;
    private final long statoPartenza;
    private final long statoArrivo;
    private final int armate;

    /**
     * Crea un oggetto contenente i dati relativi ad uno spostamento.
     * @param giocatore il giocatore che effettua lo spostamento
     * @param statoPartenza id dello stato da cui vengono spostate le truppe
     * @param statoArrivo id dello stato verso cui vengono spostate le truppe
     * @param armate il numero di armate da spostare
     */
    public SpostamentoDTO(String giocatore, Long statoPartenza, Long statoArrivo, int armate) {
        if(giocatore == null || giocatore.trim().isEmpty())
            throw new DatiErratiException("Dati errati: giocatore nullo o vuoto");
        this.giocatore = giocatore;

        if(statoPartenza < 0L)
            throw new DatiErratiException("Dati errati: id stato partenza non valido");
        this.statoPartenza = statoPartenza;

        if(statoArrivo < 0L)
            throw new DatiErratiException("Dati errati: id stato arrivo on valido");
        this.statoArrivo = statoArrivo;

        if(armate <= 0)
            throw new DatiErratiException("Dati errati: numero armate zero o negativo");
        this.armate = armate;
    }

    /**
     * Restituisce il nome del giocatore che effettua lo spostamento.
     * @return nome del giocatore
     */
    public String getGiocatore() {
        return giocatore;
    }

    /**
     * Restituisce l'id dello stato da cui vengono spostate le truppe
     * @return l'id dello stato da cui vengono spostate le truppe
     */
    public long getStatoPartenza() {
        return statoPartenza;
    }

    /**
     * Restituisce l'id dello stato verso cui vengono spostate le truppe
     * @return l'id dello stato verso cui vengono spostate le truppe
     */
    public long getStatoArrivo() {
        return statoArrivo;
    }

    /**
     * Restituisce il numero di armate da spostare.
     * @return il numero di armate da spostare
     */
    public int getArmate() {
        return armate;
    }
}
