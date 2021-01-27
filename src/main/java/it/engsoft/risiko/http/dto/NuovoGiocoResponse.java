package it.engsoft.risiko.http.dto;

import it.engsoft.risiko.data.model.Partita;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe utilizzata per gestire i dati in uscita relativi all'avvio di una partita.
 */
public final class NuovoGiocoResponse {
    private final List<GiocatoreDTO> giocatori;
    private final String giocatoreAttivo;
    private final MappaDTO mappa;

    /**
     * Crea un oggetto contenente i dati necessari all'avvio di una partita.
     * @param partita la partita da cui prendere i dati
     */
    public NuovoGiocoResponse(Partita partita) {
        this.giocatori = partita.getGiocatori().stream()
                .map(GiocatoreDTO::new)
                .collect(Collectors.toList());
        this.giocatoreAttivo = partita.getGiocatoreAttivo().getNome();
        this.mappa = new MappaDTO(partita.getMappa());
    }

    /**
     * Restituisce la lista dei giocatori.
     * @return lista dei giocatori in formato GiocatoreDTO
     */
    public List<GiocatoreDTO> getGiocatori() {
        return giocatori;
    }

    /**
     * Restituisce il nome del primo giocatore di turno.
     * @return il nome del primo giocatore di turno
     */
    public String getGiocatoreAttivo() {
        return giocatoreAttivo;
    }

    /**
     * La mappa su cui ha luogo la partita.
     * @return mappa in formato MappaDTO
     */
    public MappaDTO getMappa() {
        return mappa;
    }
}
