package it.engsoft.risiko.rest.dto;

import it.engsoft.risiko.data.model.Mappa;

public final class CompactMappaDTO {
    private final Long id;
    private final String nome;
    private final String descrizione;
    private final int numMinGiocatori;
    private final int numMaxGiocatori;
    private final int numContinenti;

    public CompactMappaDTO(Mappa mappa) {
        this.id = mappa.getId();
        this.nome = mappa.getNome();
        this.descrizione = mappa.getDescrizione();
        this.numMaxGiocatori = mappa.getNumMaxGiocatori();
        this.numMinGiocatori = mappa.getNumMinGiocatori();
        this.numContinenti = mappa.getContinenti().size();
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

    public int getNumContinenti() {
        return numContinenti;
    }
}
