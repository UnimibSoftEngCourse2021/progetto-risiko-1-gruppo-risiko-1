package it.engsoft.risiko.dao;

import it.engsoft.risiko.data.model.Mappa;

public class CompactMappaDAO {
    private Long id;
    private String nome;
    private String descrizione;
    private int numMinGiocatori;
    private int numMaxGiocatori;
    private int numContinenti;

    public CompactMappaDAO(Mappa mappa) {
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
