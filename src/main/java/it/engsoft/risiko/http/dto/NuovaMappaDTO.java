package it.engsoft.risiko.http.dto;

import it.engsoft.risiko.exceptions.DatiErratiException;

import java.util.List;

/**
 * Classe contenente i dati inviati dall'utente relativi ad una nuova mappa.
 */
public final class NuovaMappaDTO {
    private final String nome;
    private final String descrizione;
    private final int numMinGiocatori;
    private final int numMaxGiocatori;
    private final List<NuovoContinenteDTO> continenti;

    /**
     * Crea un oggetto contenente i dati relativi ad una nuova mappa.
     * @param nome il nome della mappa
     * @param descrizione la descrizione della mappa
     * @param numMinGiocatori il numero minimo di giocatori che possono giocare sulla mappa
     * @param numMaxGiocatori il numero massimo di giocatori che possono giocare sulla mappa
     * @param continenti la lista di continenti presenti nella mappa
     */
    public NuovaMappaDTO(String nome, String descrizione, int numMinGiocatori, int numMaxGiocatori, List<NuovoContinenteDTO> continenti) {
        if(nome == null || nome.trim().isEmpty())
            throw new DatiErratiException("Dati errati: nome mappa nullo o vuoto");
        this.nome = nome;

        if(descrizione == null)
            throw new DatiErratiException("Dati errati: descrizione nulla");
        this.descrizione = descrizione;

        if(numMinGiocatori < 2 || numMaxGiocatori > 8)
            throw new DatiErratiException("Dati errati: numero giocatori min/max invalido");
        this.numMinGiocatori = numMinGiocatori;
        this.numMaxGiocatori = numMaxGiocatori;

        if(continenti == null || continenti.size() < 4 || continenti.size() > 7)
            throw new DatiErratiException("Dati errati: numero continenti non valido");

        this.continenti = continenti;
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
     * Restituisce il numero massimo di giocatori che possono giocare sulla mappa
     * @return il numero massimo di giocatori che possono giocare sulla mappa
     */
    public int getNumMaxGiocatori() {
        return numMaxGiocatori;
    }

    /**
     * Restituisce la lista dei continenti presenti nella mappa.
     * @return lista di continenti in formato NuovoContinenteDTO
     */
    public List<NuovoContinenteDTO> getContinenti() {
        return continenti;
    }
}
