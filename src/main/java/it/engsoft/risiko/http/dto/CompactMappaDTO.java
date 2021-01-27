package it.engsoft.risiko.http.dto;

import it.engsoft.risiko.data.model.Mappa;

/**
 * Classe utilizzata per gestire i dati in uscita relativi alle mappe.
 * Fornisce dati sintetici.
 */
public final class CompactMappaDTO {
    private final Long id;
    private final String nome;
    private final String descrizione;
    private final int numMinGiocatori;
    private final int numMaxGiocatori;
    private final int numContinenti;
    private final int numStati;

    /**
     * Crea un oggetto contenente i dati più significativi di una mappa.
     * @param mappa La mappa da cui prendere i dati
     */
    public CompactMappaDTO(Mappa mappa) {
        this.id = mappa.getId();
        this.nome = mappa.getNome();
        this.descrizione = mappa.getDescrizione();
        this.numMaxGiocatori = mappa.getNumMaxGiocatori();
        this.numMinGiocatori = mappa.getNumMinGiocatori();
        this.numContinenti = mappa.getContinenti().size();
        this.numStati = mappa.getStati().size();
    }

    /**
     * Restituisce l'id della mappa.
     * @return l'id della mappa
     */
    public Long getId() {
        return id;
    }

    /**
     * Restituisce il nome della mappa.
     * @return il nome della mappa
     */
    public String getNome() {
        return nome;
    }

    /**
     * Restituisce la descrizione della mappa.
     * @return la descrizione della mappa
     */
    public String getDescrizione() {
        return descrizione;
    }

    /**
     * Restituisce il numero minimo di giocatori che possono giocare sulla mappa.
     * @return il numero minimo di giocatori che possono giocare sulla mappa
     */
    public int getNumMinGiocatori() {
        return numMinGiocatori;
    }

    /**
     * Restituisce il numero massimo di giocatori che possono giocare sulla mappa.
     * @return il numero massimo di giocatori che possono giocare sulla mappa
     */
    public int getNumMaxGiocatori() {
        return numMaxGiocatori;
    }

    /**
     * Restituisce il numero di continenti della mappa.
     * @return il numero di continenti della mappa
     */
    public int getNumContinenti() {
        return numContinenti;
    }

    /**
     * Restituisce il numero di stati della mappa.
     * @return il numero di stati della mappa.
     */
    public int getNumStati() {
        return numStati;
    }
}
