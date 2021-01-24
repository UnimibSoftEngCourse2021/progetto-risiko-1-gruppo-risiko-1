package it.engsoft.risiko.rest.DTO;

import it.engsoft.risiko.data.model.Partita;

import java.util.List;
import java.util.stream.Collectors;

public final class NuovoGiocoResponse {
    private final List<GiocatoreDTO> giocatori;
    private final String giocatoreAttivo;
    private final MappaDTO mappa;

    public NuovoGiocoResponse(Partita partita) {
        this.giocatori = partita.getGiocatori().stream()
                .map(GiocatoreDTO::new)
                .collect(Collectors.toList());
        this.giocatoreAttivo = partita.getGiocatoreAttivo().getNome();
        this.mappa = new MappaDTO(partita.getMappa());
    }

    public List<GiocatoreDTO> getGiocatori() {
        return giocatori;
    }

    public String getGiocatoreAttivo() {
        return giocatoreAttivo;
    }

    public MappaDTO getMappa() {
        return mappa;
    }
}
