package it.engsoft.risiko.http.dto;

import it.engsoft.risiko.exceptions.DatiErratiException;

/**
 * Classe utilizzata per gestire i dati in entrata relativi ad un attacco.
 */
public final class AttaccoDTO {
    private final String giocatore;
    private final long attaccante;
    private final long difensore;
    private final int armate;

    /**
     * Crea un oggetto contenente i dati dell'attacco inviati dall'utente.
     * @param giocatore Il nome del giocatore che lancia l'attacco
     * @param attaccante L'id dello stato da cui parte l'attacco
     * @param difensore L'id dello stato che subisce l'attacco
     * @param armate Il numero di armate utilizzate dall'attaccante (tra 1 e 3)
     */
    public AttaccoDTO(String giocatore, Long attaccante, Long difensore, int armate) {
        if(giocatore == null || giocatore.trim().isEmpty())
            throw new DatiErratiException("Dati errati: giocatore nullo o vuoto");
        this.giocatore = giocatore;

        if(attaccante < 0L)
            throw new DatiErratiException("Dati errati: id attaccante non valido");
        this.attaccante = attaccante;

        if(difensore < 0L)
            throw new DatiErratiException("Dati errati: id difensore non valido");
        this.difensore = difensore;

        if(armate <= 0)
            throw new DatiErratiException("Dati errati: numero armate negativo");
        this.armate = armate;
    }

    /**
     * Restituisce il nome del giocatore attaccante.
     * @return il nome del giocatore attaccante
     */
    public String getGiocatore() {
        return giocatore;
    }

    /**
     * Restituisce l'id dello stato attaccante.
     * @return l'id dello stato attaccante
     */
    public long getAttaccante() {
        return attaccante;
    }

    /**
     * Restituisce l'id dello stato difensore.
     * @return l'id dello stato difensore
     */
    public long getDifensore() {
        return difensore;
    }

    /**
     * Restituisce il numero di armate attaccanti.
     * @return il numero di armate attaccanti
     */
    public int getArmate() {
        return armate;
    }
}
