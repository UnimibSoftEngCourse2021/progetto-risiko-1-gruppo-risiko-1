package it.engsoft.risiko.data.model;

import it.engsoft.risiko.exceptions.ModelDataException;

import java.util.List;

public class Partita {
    private final List<Giocatore> giocatori;
    private final Mappa mappa;
    private final Modalita modalita;
    private final Mazzo mazzo;
    private Giocatore giocatoreAttivo;
    private FaseTurno faseTurno;
    private Combattimento combattimento;
    private boolean fasePreparazione;
    private boolean territoriOccupati;
    private boolean conquista;

    public enum FaseTurno {INIZIALIZZAZIONE, RINFORZI, COMBATTIMENTI, SPOSTAMENTO}

    public Partita(Mappa mappa, List<Giocatore> giocatori, Modalita modalita) {
        if (mappa == null || giocatori == null || modalita == null)
            throw new ModelDataException("Parametri del costruttore null (partita)");

        this.mappa = mappa;
        this.modalita = modalita;
        this.giocatori = giocatori;
        faseTurno = FaseTurno.INIZIALIZZAZIONE;
        fasePreparazione = true;
        conquista = false;
        combattimento = null;

        territoriOccupati = false;
        mazzo = new Mazzo(mappa.getStati());
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

    public Mappa getMappa() {
        return mappa;
    }

    public Modalita getModalita() {
        return modalita;
    }

    public Mazzo getMazzo() { return mazzo; }

    public FaseTurno getFaseTurno() {
        return faseTurno;
    }

    public void setFaseTurno(FaseTurno faseTurno) {
        this.faseTurno = faseTurno;
    }

    public Combattimento getCombattimento() {
        return combattimento;
    }

    public void setCombattimento(Combattimento combattimento) {
        this.combattimento = combattimento;
    }

    public boolean isFasePreparazione() {
        return fasePreparazione;
    }

    public void setFasePreparazione(boolean fasePreparazione) {
        if (!territoriOccupati || !this.fasePreparazione || giocatori.stream().anyMatch(g -> g.getTruppeDisponibili() > 0))
            throw new ModelDataException("Devi prima effettuare la distribuzione degli stati e l'occupazione iniziale");

        this.fasePreparazione = fasePreparazione;
    }

    public boolean getConquista() {
        return conquista;
    }

    public void setConquista() {
        conquista = true;
    }

    public void nuovoTurno() {
        if (fasePreparazione)
            throw new ModelDataException("Devi prima finire la fase di preparazione");
        if (giocatoreAttivo.getTruppeDisponibili() > 0)
            throw new ModelDataException("Il giocatore di turno deve prima posizionare le sue armate");

        giocatoreAttivo = prossimoGiocatoreDisponibile();
        faseTurno = FaseTurno.INIZIALIZZAZIONE;
        conquista = false;
    }

    public void setNuovoGiocatoreAttivoPreparazione() {
        if (!fasePreparazione)
            throw new ModelDataException("La fase di preparazione è finita");
        Giocatore prossimo = prossimoGiocatoreDisponibile();

        if (prossimo.getTruppeDisponibili() == 0) {
            fasePreparazione = false;
            giocatoreAttivo = giocatori.get(0);
        } else {
            giocatoreAttivo = prossimo;
        }
    }

    private Giocatore prossimoGiocatoreDisponibile() {
        int index = giocatori.indexOf(giocatoreAttivo);
        Giocatore ris = null;
        boolean set = false;
        while (!set) {
            index = (index + 1) % giocatori.size();
            if (!giocatori.get(index).isEliminato()) {
                ris = giocatori.get(index);
                set = true;
            }
        }
        return ris;
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
        return giocatori.stream().noneMatch(g -> g.getStati().isEmpty());
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
