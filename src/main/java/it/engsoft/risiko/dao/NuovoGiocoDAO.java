package it.engsoft.risiko.dao;

import it.engsoft.risiko.model.Partita;

import java.util.List;
import java.util.stream.Collectors;

public class NuovoGiocoDAO {
    public List<GiocatoreDAO> giocatori;
    public String giocatoreAttivo;
    public MappaDAO mappa;

    public NuovoGiocoDAO(Partita partita) {
        this.giocatori = partita.getGiocatori().stream()
                .map(GiocatoreDAO::new)
                .collect(Collectors.toList());
        this.giocatoreAttivo = partita.getGiocatoreAttivo().getNome();
        this.mappa = new MappaDAO(partita.getMappa());
    }

    public List<GiocatoreDAO> getGiocatori() {
        return giocatori;
    }

    public String getGiocatoreAttivo() {
        return giocatoreAttivo;
    }

    public MappaDAO getMappa() {
        return mappa;
    }
}
