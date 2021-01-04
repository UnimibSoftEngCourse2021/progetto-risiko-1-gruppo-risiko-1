package it.engsoft.risiko.model;

import java.util.ArrayList;
import java.util.List;

public class Partita {
    private List<Giocatore> giocatori = new ArrayList<>();
    private Giocatore giocatoreAttivo;
    private Turno turno;
    private Mappa mappa;
    private Modalita modalita;

    public enum Modalita{
        VELOCE,
        NORMALE,
        LENTA
    };

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

    public void iniziaPrimoTurno() {
        setGiocatoreAttivo(giocatori.get(0));
        turno = new Turno(giocatoreAttivo, 1);
    }

    public void nuovoTurno() {
        setProssimoGiocatoreAttivo();
        turno = new Turno(giocatoreAttivo, turno.getNumero() + 1);
    }

    public void setProssimoGiocatoreAttivo() {
        int index = giocatori.indexOf(giocatoreAttivo);

        boolean set  = false;
        while(!set) {
            index = (index + 1) % giocatori.size();
            if (!giocatori.get(index).isEliminato()) {
                giocatoreAttivo = giocatori.get(index);
                set = true;
            }
        }
    }

    public void assegnaArmateIniziali() {
        // TODO: implementare un meccanismo per stabilire quante
        int nArmate = 30;
        giocatori.forEach(giocatore -> giocatore.setTruppeDisponibili(nArmate));
    }
}
