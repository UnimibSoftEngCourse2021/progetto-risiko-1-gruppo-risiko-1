package it.engsoft.risiko.model;

import java.util.ArrayList;
import java.util.List;

public class Partita {
    private List<Giocatore> giocatori = new ArrayList<>();
    private Giocatore giocatoreAttivo;
    private Turno turno;
    private Mappa mappa;
    private Modalita modalita;
    private List<CartaTerritorio> mazzo = new ArrayList<>();
    private boolean fasePreparazione;

    public enum Modalita {
        VELOCE,
        RIDOTTA,
        COMPLETA;

        public static Modalita valutaModalita(String modalita) {
            if (modalita.equalsIgnoreCase(Partita.Modalita.COMPLETA.toString())) {
                return Partita.Modalita.COMPLETA;
            } else if (modalita.equalsIgnoreCase(Partita.Modalita.RIDOTTA.toString())) {
                return Partita.Modalita.RIDOTTA;
            } else if (modalita.equalsIgnoreCase(Partita.Modalita.VELOCE.toString())) {
                return Partita.Modalita.VELOCE;
            }

            return Modalita.COMPLETA;
        }
    }


    public List<Giocatore> getGiocatori() {
        return giocatori;
    }

    public void setGiocatori(List<Giocatore> giocatori) {
        this.giocatori = giocatori;
    }

    public Giocatore getGiocatoreAttivo() {
        return giocatoreAttivo;
    }

    public void setGiocatoreAttivo(Giocatore giocatoreAttivo) {
        this.giocatoreAttivo = giocatoreAttivo;
    }

    public Turno getTurno() {
        return turno;
    }

    public void setTurno(Turno turno) {
        this.turno = turno;
    }

    public Mappa getMappa() {
        return mappa;
    }

    public void setMappa(Mappa mappa) {
        this.mappa = mappa;
    }

    public Modalita getModalita() {
        return modalita;
    }

    public void setModalita(Modalita modalita) {
        this.modalita = modalita;
    }

    public List<CartaTerritorio> getMazzo() {
        return mazzo;
    }

    public boolean isFasePreparazione() { return fasePreparazione; }

    public void setFasePreparazione(boolean fasePreparazione) { this.fasePreparazione = fasePreparazione; }

    public void iniziaPrimoTurno() {
        setGiocatoreAttivo(giocatori.get(0));
        turno = new Turno(1);
    }

    public void nuovoTurno() {
        setProssimoGiocatoreAttivo();
        turno = new Turno(turno.getNumero() + 1);
    }

    public void setProssimoGiocatoreAttivo() {
        int index = giocatori.indexOf(giocatoreAttivo);

        boolean set = false;
        while (!set) {
            index = (index + 1) % giocatori.size();
            if (!giocatori.get(index).isEliminato()) {
                giocatoreAttivo = giocatori.get(index);
                set = true;
            }
        }
    }

    // TODO:: nArmate = nArmate + 15;
    public void assegnaArmateIniziali() {
        int nArmate = 0;
        for (int i = 5; i < this.mappa.getStati().size(); i = i + 5) {
            nArmate = nArmate + 7;
        }
        nArmate = nArmate / giocatori.size();
        int finalNArmate = nArmate;
        giocatori.forEach(giocatore -> giocatore.setTruppeDisponibili(finalNArmate));
    }
}
