package it.engsoft.risiko.rest.dto;

import it.engsoft.risiko.data.model.Mappa;

import java.util.List;
import java.util.stream.Collectors;

public final class MappaDTO {
    private final Long id;
    private final String nome;
    private final String descrizione;
    private final int numMinGiocatori;
    private final int numMaxGiocatori;
    private final List<ContinenteDTO> continenti;

    public MappaDTO(Mappa mappa) {
        this.id = mappa.getId();
        this.nome = mappa.getNome();
        this.descrizione = mappa.getDescrizione();
        this.numMaxGiocatori = mappa.getNumMaxGiocatori();
        this.numMinGiocatori = mappa.getNumMinGiocatori();
        this.continenti = mappa.getContinenti().stream().map(ContinenteDTO::new)
                .collect(Collectors.toList());
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public int getNumMinGiocatori() {
        return numMinGiocatori;
    }

    public int getNumMaxGiocatori() {
        return numMaxGiocatori;
    }

    public List<ContinenteDTO> getContinenti() {
        return continenti;
    }
}
