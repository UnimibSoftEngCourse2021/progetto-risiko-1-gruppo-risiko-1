package it.engsoft.risiko.dao;

import it.engsoft.risiko.model.Partita;

import java.util.List;
import java.util.stream.Collectors;

public class NuovoGiocoDAO {
    public List<GiocatoreDAO> giocatori;
    public String giocatoreAttivo;
    public int turno;
    public MappaDAO mappa;
    public String modalita;

    public NuovoGiocoDAO(Partita partita) {
        this.giocatori = partita.giocatori.stream()
                .map(GiocatoreDAO::new)
                .collect(Collectors.toList());
        this.giocatoreAttivo = partita.giocatoreAttivo.getNome();
        this.turno = partita.turno.getNumero();
        this.mappa = new MappaDAO(partita.mappa);
        this.modalita = partita.modalita.toString();
    }

    public List<GiocatoreDAO> getGiocatori() {
        return giocatori;
    }

    public String getGiocatoreAttivo() {
        return giocatoreAttivo;
    }

    public int getTurno() {
        return turno;
    }

    public MappaDAO getMappa() {
        return mappa;
    }

    public String getModalita() {
        return modalita;
    }
}
