package it.engsoft.risiko.data.model;

import it.engsoft.risiko.exceptions.ModelDataException;

import java.util.List;

/**
 * Rappresenta una partita di Risiko: ne contiene tutte le informazioni principali e ne assicura lo svolgimento
 * corretto.
 */
public class Partita {
    private final List<Giocatore> giocatori;
    private final Mappa mappa;
    private final Mazzo mazzo;
    private Giocatore giocatoreAttivo;
    private FaseTurno faseTurno;
    private Combattimento combattimento;
    private boolean fasePreparazione;
    private boolean territoriOccupati;
    private boolean conquista;

    public enum FaseTurno {INIZIALIZZAZIONE, RINFORZI, COMBATTIMENTI, SPOSTAMENTO}

    /**
     * Crea una partita specificandone le informazioni essenziali.
     * @param mappa la mappa con cui giocare
     * @param giocatori l'elenco dei giocatori
     */
    public Partita(Mappa mappa, List<Giocatore> giocatori) {
        if (mappa == null || giocatori == null)
            throw new ModelDataException("Parametri del costruttore null (partita)");

        this.mappa = mappa;
        this.giocatori = giocatori;
        faseTurno = FaseTurno.INIZIALIZZAZIONE;
        fasePreparazione = true;
        conquista = false;
        combattimento = null;

        territoriOccupati = false;
        mazzo = new Mazzo(mappa.getStati());
        assegnaArmateIniziali();
    }

    /**
     * Ritorna l'elenco dei giocatori.
     * @return l'elenco dei giocatori
     */
    public List<Giocatore> getGiocatori() {
        return giocatori;
    }

    /**
     * Ritorna il giocatore attivo al momento. Se nessun c'è nessun giocatore attivo (solo la prima volta succede),
     * viene tornato il primo giocatore dell'elenco.
     * @return il giocatore attivo
     */
    public Giocatore getGiocatoreAttivo() {
        if (!territoriOccupati)
            throw new ModelDataException("Prima di iniziare a giocare devi occupare tutti i territori con un armata");

        if (giocatoreAttivo == null) // solo la prima volta
            giocatoreAttivo = giocatori.get(0);

        return giocatoreAttivo;
    }

    /**
     * Ritorna la mappa di gioco.
     * @return la mappa di gioco
     */
    public Mappa getMappa() {
        return mappa;
    }

    /**
     * Ritorna il mazzo di carte territorio.
     * @return il mazzo di carte territorio
     */
    public Mazzo getMazzo() { return mazzo; }

    /**
     * Ritorna la fase del turno in cui ci si trova.
     * @return la fase del turno
     */
    public FaseTurno getFaseTurno() {
        return faseTurno;
    }

    /**
     * Imposta la fase del turno.
     * @param faseTurno la nuova fase del turno
     */
    public void setFaseTurno(FaseTurno faseTurno) {
        this.faseTurno = faseTurno;
    }

    /**
     * Ritorna il combattimento in corso se ve ne è uno, null altrimenti
     * @return il combattimento in corso se ve ne è uno, null altrimenti
     */
    public Combattimento getCombattimento() {
        return combattimento;
    }

    /**
     * Imposta il nuovo combattimento in corso.
     * @param combattimento il nuovo combattimento in corso
     */
    public void setCombattimento(Combattimento combattimento) {
        this.combattimento = combattimento;
    }

    /**
     * Ritorna true se ci si trova ancora nella fase di preparazione, in cui ogni giocatore a turno deve posizionare 3
     * rinforzi.
     * @return true se ci si trova nella fase di preparazione
     */
    public boolean isFasePreparazione() {
        return fasePreparazione;
    }

    /**
     * Ritorna true se in questo turno è stato conquistato almeno un territorio.
     * @return true se in questo turno è stato conquistato almeno un territorio
     */
    public boolean getConquista() {
        return conquista;
    }

    /**
     * Tiene traccia del fatto che in questo turno è avvenuta una conquista.
     */
    public void registraConquista() {
        conquista = true;
    }

    /**
     * Inizia un nuovo turno.
     */
    public void nuovoTurno() {
        if (fasePreparazione)
            throw new ModelDataException("Devi prima finire la fase di preparazione");
        if (giocatoreAttivo.getArmateDisponibili() > 0)
            throw new ModelDataException("Il giocatore di turno deve prima posizionare le sue armate");

        giocatoreAttivo = prossimoGiocatoreDisponibile();
        faseTurno = FaseTurno.INIZIALIZZAZIONE;
        conquista = false;
    }

    /**
     * Se in fase di preparazione, imposta il prossimo giocatore attivo. Se tale giocatore non ha più armate da
     * posizionare, allora pone fine alla fase di preparazione e inizia il primo turno.
     */
    public void setNuovoGiocatoreAttivoPreparazione() {
        if (!fasePreparazione)
            throw new ModelDataException("La fase di preparazione è finita");
        Giocatore prossimo = prossimoGiocatoreDisponibile();

        if (prossimo.getArmateDisponibili() == 0) {
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
        giocatori.forEach(giocatore -> giocatore.setArmateDisponibili(finalNArmate));
    }

    private boolean territoriAssegnati() {
        return giocatori.stream().noneMatch(g -> g.getStati().isEmpty());
    }

    /**
     * Effettua l'occupazione iniziale dei territori: ogni territorio viene occupato con un'armata del giocatore
     * proprietario.
     * Può (e deve) essere effettuata solo una volta per ogni partita, prima della fase di preparazione.
     */
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
