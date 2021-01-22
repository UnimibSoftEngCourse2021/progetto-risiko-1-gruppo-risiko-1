package it.engsoft.risiko.dao;

import it.engsoft.risiko.data.model.Mappa;

import java.util.List;
import java.util.stream.Collectors;

public class MappaDAO {
    private Long id;
    private String nome;
    private String descrizione;
    private int numMinGiocatori;
    private int numMaxGiocatori;
    private List<ContinenteDAO> continenti;

    public MappaDAO(Mappa mappa) {
        this.id = mappa.getId();
        this.nome = mappa.getNome();
        this.descrizione = mappa.getDescrizione();
        this.numMaxGiocatori = mappa.getNumMaxGiocatori();
        this.numMinGiocatori = mappa.getNumMinGiocatori();
        this.continenti = mappa.getContinenti().stream().map(ContinenteDAO::new)
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

    public List<ContinenteDAO> getContinenti() {
        return continenti;
    }
}
