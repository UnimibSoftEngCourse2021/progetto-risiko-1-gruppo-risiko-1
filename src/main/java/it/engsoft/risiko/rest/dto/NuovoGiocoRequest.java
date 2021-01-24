package it.engsoft.risiko.rest.dto;

import it.engsoft.risiko.exceptions.DatiErratiException;
import java.util.List;

/**
 * Classe utilizzata per gestire i dati in entrata relativi all'avvio di una partita.
 */
public final class NuovoGiocoRequest {
    private final List<String> giocatori;
    private final long mappaId;
    private final String mod;
    private final boolean unicoObiettivo;

    /**
     * Crea un oggetto contenente i dati inviati dall'utente relativi all'avvio di una partita.
     * @param giocatori la lista di giocatori che partecipano alla partita
     * @param mappaId l'id della mappa su cui viene giocata la partita
     * @param mod la modalità della partita
     * @param unicoObiettivo booleano che indica se tutti i giocatori devono avere lo stesso obiettivo standard
     */
    public NuovoGiocoRequest(List<String> giocatori, Long mappaId, String mod, boolean unicoObiettivo) {
        if(giocatori == null)
            throw new DatiErratiException("Dati errati: lista giocatori nulla");
        this.giocatori = giocatori;

        if(mappaId < 0L)
            throw new DatiErratiException("Dati errati: id mappa non valido");
        this.mappaId = mappaId;

        if(mod == null || mod.trim().isEmpty())
            throw new DatiErratiException("Dati errati: modalita' nulla o mancante");
        this.mod = mod;

        this.unicoObiettivo = unicoObiettivo;
    }

    /**
     * Restituisce i nomi dei giocatori.
     * @return la lista contenente i nomi dei giocatori
     */
    public List<String> getGiocatori() {
        return giocatori;
    }

    /**
     * Restituisce l'id della mappa indicata.
     * @return id della mappa indicata
     */
    public long getMappaId() {
        return mappaId;
    }

    /**
     * Restituisce la modalità della partita.
     * @return modalità della partita
     */
    public String getMod() {
        return mod;
    }

    /**
     * Restituisce true se tutti i giocatori devono avere lo stesso obiettivo, false altrimenti.
     * @return true se tutti i giocatori devono avere lo stesso obiettivo, false altrimenti
     */
    public boolean isUnicoObiettivo() {
        return unicoObiettivo;
    }
}
