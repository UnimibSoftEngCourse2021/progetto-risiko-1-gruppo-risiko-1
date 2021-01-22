package it.engsoft.risiko.data.model;

import it.engsoft.risiko.exceptions.ModelDataException;

import java.util.ArrayList;
import java.util.List;

public class Partita {
    private final List<Giocatore> giocatori;
    private final Mappa mappa;
    private final Modalita modalita;
    private final List<CartaTerritorio> mazzo;
    private Giocatore giocatoreAttivo;
    private Turno turno;
    private boolean fasePreparazione;
    private boolean territoriOccupati;

    public Partita(Mappa mappa, List<Giocatore> giocatori, Modalita modalita) {
        if (mappa == null || giocatori == null || modalita == null)
            throw new ModelDataException("Parametri del costruttore null (partita)");

        this.mappa = mappa;
        this.giocatori = giocatori;
        this.modalita = modalita;
        fasePreparazione = true;
        territoriOccupati = false;
        mazzo = new ArrayList<>();
        assegnaArmateIniziali();
    }

    public List<Giocatore> getGiocatori() {
        return giocatori;
    }

    public Giocatore getGiocatoreAttivo() {
        if (!territoriOccupati)
            throw new ModelDataException("Prima di iniziare a giocare devi occupare tutti i territori con un armata");

        if (giocatoreAttivo == null) // solo la prima volta
            giocatoreAttivo = giocatori.get(0);

        return giocatoreAttivo;
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

    public Modalita getModalita() {
        return modalita;
    }

    public List<CartaTerritorio> getMazzo() {
        return mazzo;
    }

    public boolean isFasePreparazione() {
        return fasePreparazione;
    }

    public void setFasePreparazione(boolean fasePreparazione) {
        if (!territoriOccupati || !this.fasePreparazione || giocatori.stream().anyMatch(g -> g.getTruppeDisponibili() > 0))
            throw new ModelDataException("Devi prima effettuare la distribuzione degli stati e l'occupazione iniziale");

        this.fasePreparazione = fasePreparazione;
    }

    public void iniziaPrimoTurno() {
        if (fasePreparazione)
            throw new ModelDataException("Devi prima finire la fase di preparazione");

        giocatoreAttivo = giocatori.get(0);
        turno = new Turno(1);
    }

    public void nuovoTurno() {
        if (fasePreparazione)
            throw new ModelDataException("Devi prima finire la fase di preparazione");
        if (giocatoreAttivo.getTruppeDisponibili() > 0)
            throw new ModelDataException("Il giocatore di turno deve prima posizionare le sue armate");

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

    private void assegnaArmateIniziali() {
        int nArmate = 0;
        for (int i = 5; i < this.mappa.getStati().size(); i = i + 5) {
            nArmate = nArmate + 15;
        }
        nArmate = nArmate / giocatori.size();
        int finalNArmate = nArmate;
        giocatori.forEach(giocatore -> giocatore.setTruppeDisponibili(finalNArmate));
    }

    private boolean territoriAssegnati() {
        return giocatori.stream().noneMatch(g -> g.getStati().size() == 0);
    }

    public void occupazioneInizialeTerritori() {
        if (!territoriAssegnati())
            throw new ModelDataException("Prima devi distribuire le carte territorio");
        if (territoriOccupati)
            throw new ModelDataException("I territori sono già stati occupati");

        giocatori.forEach(
                giocatore -> giocatore.getStati().forEach(
                        stato -> {
                            stato.aggiungiArmate(1);
                            giocatore.modificaTruppeDisponibili(-1);
                        }
                )
        );
        territoriOccupati = true;
    }
}
