package it.engsoft.risiko.rest.dto;

import it.engsoft.risiko.data.model.Mappa;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe utilizzata per gestire i dati in uscita relativi alle mappe.
 * Fornisce dati completi.
 */
public final class MappaDTO {
    private final Long id;
    private final String nome;
    private final String descrizione;
    private final int numMinGiocatori;
    private final int numMaxGiocatori;
    private final List<ContinenteDTO> continenti;

    /**
     * Crea un oggetto contenente i dati relativi ad una mappa.
     * @param mappa La mappa da cui prendere i dati
     */
    public MappaDTO(Mappa mappa) {
        this.id = mappa.getId();
        this.nome = mappa.getNome();
        this.descrizione = mappa.getDescrizione();
        this.numMaxGiocatori = mappa.getNumMaxGiocatori();
        this.numMinGiocatori = mappa.getNumMinGiocatori();
        this.continenti = mappa.getContinenti().stream().map(ContinenteDTO::new)
                .collect(Collectors.toList());
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
     * Restituisce la lista di continenti presenti nella mappa.
     * @return lista di continenti in formato ContinenteDTO
     */
    public List<ContinenteDTO> getContinenti() {
        return continenti;
    }
}
