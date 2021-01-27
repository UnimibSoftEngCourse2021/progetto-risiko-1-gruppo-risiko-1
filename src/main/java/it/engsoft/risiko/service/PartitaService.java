package it.engsoft.risiko.service;

import it.engsoft.risiko.data.creators.ObiettivoFactory;
import it.engsoft.risiko.http.dto.*;
import it.engsoft.risiko.exceptions.*;
import it.engsoft.risiko.data.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Questo service gestisce la partita.
 */
@Service
public class PartitaService {
    private final MappaService mappaService;

    /**
     * Crea un istanza di mappaService.
     * @param mappaService il service che si vuole istanziare.
     */
    @Autowired
    public PartitaService(MappaService mappaService) { this.mappaService = mappaService; }

    /**
     * Crea una nuova partita.
     * @param nuovoGiocoRequest un data transfer object contenente le informazioni necessarie per crearla.
     * @return l'oggetto partita.
     */
    public Partita nuovoGioco(NuovoGiocoRequest nuovoGiocoRequest) {
        Modalita modalita = Modalita.valutaModalita(nuovoGiocoRequest.getMod());

        // istanzia la mappa
        Mappa mappa = mappaService.getMappa(nuovoGiocoRequest.getMappaId(), modalita);

        // controllo sul valore minimo e massimo di giocatori ammessi dalla mappa
        if (mappa.getNumMaxGiocatori() < nuovoGiocoRequest.getGiocatori().size() ||
                mappa.getNumMinGiocatori() > nuovoGiocoRequest.getGiocatori().size())
            throw new DatiErratiException("Dati errati: valore minimo/massimo di giocatori non ammesso");

        // creazione dei nuovi giocatori
        List<Giocatore> giocatori = nuovoGiocoRequest.getGiocatori()
                .stream()
                .map(Giocatore::new)
                .collect(Collectors.toList());

        // assegna gli obiettivi ai giocatori
        ObiettivoFactory obFactory = new ObiettivoFactory(mappa, giocatori, nuovoGiocoRequest.isUnicoObiettivo());
        giocatori.forEach(g -> g.setObiettivo(obFactory.getNuovoObiettivo()));

        // scegliamo casualmente un ordine di giocatori
        Collections.shuffle(giocatori);

        Partita partita = new Partita(mappa, giocatori);

        // distribuisci le carte territorio, comprende assegnazione degli stati
        partita.getMazzo().distribuisciCarte(giocatori);

        partita.occupazioneInizialeTerritori();

        return partita;
    }

    /**
     * Prepara il nuovo turno in modo che possa essere usato dagli altri metodi.
     * @param partita l'oggetto partita al quale si riferisce iniziaTurno.
     * @return un data transfer object contenente le informazioni calcolate dal metodo.
     */
    public IniziaTurnoDTO iniziaTurno(Partita partita) {
        if (partita.isFasePreparazione())
            throw new MossaIllegaleException("Mossa illegale: impossibile iniziare il turno in fase di preparazione");

        if (!partita.getFaseTurno().equals(Partita.FaseTurno.INIZIALIZZAZIONE))
            throw new MossaIllegaleException("Mossa illegale: turno già iniziato");

        Giocatore giocatore = partita.getGiocatoreAttivo();

        // calcolo armate bonus date al giocatore attivo dagli stati conquistati
        int armateStati = Math.max(1, giocatore.getStati().size() / 3);

        // calcolo armate bonus date al giocatore attivo dai continenti conquistati
        int armateContinenti = 0;
        for (Continente continente : partita.getMappa().getContinenti()) {
            if (giocatore.equals(continente.getProprietario()))
                armateContinenti = armateContinenti + continente.getArmateBonus();
        }

        giocatore.modificaTruppeDisponibili(armateContinenti + armateStati);
        partita.setFaseTurno(Partita.FaseTurno.RINFORZI);
        return new IniziaTurnoDTO(giocatore.getNome(), armateStati, armateContinenti);
    }

    /**
     * Esegue il rinforzo iniziale e il rinforzo normale.
     * @param rinforzoRequest un data transfer object contenente i dati necessari per effettuare
     *                        la richiesta di rinforzo.
     * @param partita l'oggetto partita al quale si riferisce il rinforzo.
     * @return un data transfer object contenente l'esito del rinforzo
     */
    public RinforzoResponse rinforzo(RinforzoRequest rinforzoRequest, Partita partita) {
        Giocatore giocatore = partita.getGiocatoreAttivo();
        if (!giocatore.getNome().equals(rinforzoRequest.getGiocatore()))
            throw new MossaIllegaleException("Mossa illegale: rinforzo non chiamato dal giocatore attivo");

        if (partita.isFasePreparazione()) {
            int armateDaPiazzare = Math.min(3, giocatore.getArmateDisponibili());
            eseguiRinforzo(rinforzoRequest, armateDaPiazzare, partita);

            partita.setNuovoGiocatoreAttivoPreparazione();

        } else { // è un rinforzo di inizio turno
            if (!partita.getFaseTurno().equals(Partita.FaseTurno.RINFORZI))
                throw new MossaIllegaleException("Mossa illegale: rinforzo illegale se non in fase di rinforzi");

            if (giocatore.getArmateDisponibili() == 0)
                throw new MossaIllegaleException("Mossa illegale: non ci sono altre armate da piazzare");

            eseguiRinforzo(rinforzoRequest, partita.getGiocatoreAttivo().getArmateDisponibili(), partita);
        }

        return new RinforzoResponse(
                partita.getGiocatoreAttivo().getNome(),
                partita.isFasePreparazione(),
                partita.getGiocatoreAttivo().obRaggiunto()
        );
    }

    private void eseguiRinforzo(RinforzoRequest rinforzoRequest, int armateDaPiazzare, Partita partita) {
        Giocatore giocAttivo = partita.getGiocatoreAttivo();
        Mappa mappa = partita.getMappa();
        int totale = 0;
        // controlla che i rinforzi totali siano quanto richiesto
        for (Long key : rinforzoRequest.getRinforzi().keySet()) {
            totale = totale + rinforzoRequest.getRinforzi().get(key);

            // controlla che ogni stato sia del giocatore attivo
            if (!mappa.getStatoById(key).getProprietario().equals(giocAttivo))
                throw new MossaIllegaleException("Mossa illegale: almeno uno degli stati non appartiene al giocatore attivo");
        }

        if (totale != armateDaPiazzare)
            throw new DatiErratiException("Dati errati: numero armate da piazzare non valido");

        // crea ed esegue un nuovo rinforzo per ogni coppia idStato-numeroArmate della mappa in ingresso
        for (Long key : rinforzoRequest.getRinforzi().keySet()) {
            Stato stato = mappa.getStatoById(key);
            stato.aggiungiArmate(rinforzoRequest.getRinforzi().get(key));
        }

        giocAttivo.modificaTruppeDisponibili(-armateDaPiazzare);
    }

    /**
     * Incrementa le truppe disponibili del giocatore che utilizza un tris di carte territorio valido.
     * @param trisDTO un data transfer object contenente le informazioni necessarie per eseguire giocaTris.
     * @param partita l'oggetto partita al quale si riferisce il rinforzo.
     * @return il valore del tris di carte in numero di armate
     */
    public int giocaTris(TrisDTO trisDTO, Partita partita) {
        if (partita.isFasePreparazione())
            throw new MossaIllegaleException("Mossa illegale: impossibile giocare un tris in fase di preparazione");

        if (!partita.getFaseTurno().equals(Partita.FaseTurno.RINFORZI))
            throw new MossaIllegaleException("Mossa illegale: impossibile giocare un tris in fase di rinforzo");

        if (trisDTO.getTris().size() != 3)
            throw new DatiErratiException("Dati errati: passato un numero di carte diverso da 3");

        Giocatore giocatore = partita.getGiocatoreAttivo();

        if (!giocatore.getNome().equals(trisDTO.getGiocatore()))
            throw new MossaIllegaleException("Mossa illegale: gioca tris chiamato da giocatore non attivo");

        int nArmateBonus = partita.getMazzo().valutaTris(trisDTO.getTris(), giocatore);
        partita.getGiocatoreAttivo().modificaTruppeDisponibili(nArmateBonus);

        return nArmateBonus;
    }

    /**
     * Crea un oggetto combattimento e lo inizializza.
     * @param attaccoDTO un data transfer object contenente le informazioni necessarie per eseguire l' attacco.
     * @param partita l'oggetto partita al quale si riferisce l'attacco.
     */
    public void attacco(AttaccoDTO attaccoDTO, Partita partita) {
        if (partita.isFasePreparazione() || partita.getFaseTurno().equals(Partita.FaseTurno.SPOSTAMENTO))
            throw new MossaIllegaleException("Mossa illegale: impossibile attaccare in questa fase di gioco");

        if (partita.getCombattimento() != null)
            throw new MossaIllegaleException("Mossa illegale: un combattimento è già in corso");

        Giocatore giocatoreAtt = partita.getGiocatoreAttivo();

        if (giocatoreAtt.getArmateDisponibili() != 0)
            throw new MossaIllegaleException("Mossa illegale: impossibile attaccare prima di aver completato i rinforzi");

        if (!giocatoreAtt.getNome().equals(attaccoDTO.getGiocatore()))
            throw new MossaIllegaleException("Mossa illegale: non è il tuo turno");

        Stato statoAttaccante = partita.getMappa().getStatoById(attaccoDTO.getAttaccante());
        Stato statoDifensore = partita.getMappa().getStatoById(attaccoDTO.getDifensore());

        if (!giocatoreAtt.equals(statoAttaccante.getProprietario()))
            throw new MossaIllegaleException("Mossa illegale: lo stato attaccante non appartiene al giocatore attivo");

        // il costruttore verifica che gli stati siano compatibili per un attacco e che il n di armate sia valido
        Combattimento combattimento = new Combattimento(statoAttaccante, statoDifensore, attaccoDTO.getArmate());
        partita.setFaseTurno(Partita.FaseTurno.COMBATTIMENTI);
        partita.setCombattimento(combattimento);
    }

    /**
     * Termina un combattimento calcolandone il risultato grazie alle informazioni sul difensore
     * che riceve in ingresso.
     * @param difesaRequest un data transfer object contenente le informazioni necessarie per eseguire la difesa.
     * @param partita l'oggetto partita al quale si riferisce la difesa.
     * @return un data transfer object contenente le informazioni riguardo l'esito del combattimento.
     */
    public DifesaResponse difesa(DifesaRequest difesaRequest, Partita partita) {
        Combattimento combattimento = partita.getCombattimento();
        if (combattimento == null || combattimento.isEseguito())
            throw new MossaIllegaleException("Mossa illegale: non c'e' un combattimento in corso");

        Giocatore giocatoreDif = combattimento.getStatoDifensore().getProprietario();
        Giocatore giocatoreAtt = combattimento.getStatoAttaccante().getProprietario();

        if (!giocatoreDif.getNome().equals(difesaRequest.getGiocatore()))
            throw new MossaIllegaleException("Mossa illegale: giocatore difensore non valido");

        combattimento.esegui(difesaRequest.getArmate());

        if (combattimento.getConquista()) {
            partita.registraConquista(); // setta true conquista avvenuta in turno

            // gestione difensore eliminato
            if (giocatoreDif.isEliminato()) {
                giocatoreDif.setUccisore(giocatoreAtt);
                giocatoreDif.consegnaCarteTerritorio(giocatoreAtt);
            }
        }

        // se l'attaccante non ha conquistato il territorio, il combattimento è terminato
        if (!combattimento.getConquista())
            partita.setCombattimento(null);

        return new DifesaResponse(
                combattimento,
                giocatoreAtt.obRaggiunto(),
                giocatoreDif.isEliminato());
    }

    /**
     * Esegue lo spostamento di turppe obbligatorio successivo ad un attacco o un normale spostamento.
     * @param spostamentoDTO un data transfer object contenente le informazioni necessarie per eseguire lo spostamento.
     * @param partita l'oggetto partita al quale si riferisce lo spostamento.
     * @return true se il giocatore ha raggiunto il suo obiettivo e vinto la partita.
     */
    public boolean spostamentoStrategico(SpostamentoDTO spostamentoDTO, Partita partita) {
        if (partita.isFasePreparazione())
            throw new MossaIllegaleException("Mossa illegale: si e' ancora in fase di preparazione");

        Giocatore giocatore = partita.getGiocatoreAttivo();
        Stato statoArrivo = partita.getMappa().getStatoById(spostamentoDTO.getStatoArrivo());
        Stato statoPartenza = partita.getMappa().getStatoById(spostamentoDTO.getStatoPartenza());

        if (!giocatore.getNome().equals(spostamentoDTO.getGiocatore()))
            throw new MossaIllegaleException("Mossa illegale: spostamento non chiamato dal giocatore attivo");

        Combattimento combattimento = partita.getCombattimento();

        if (combattimento != null && !combattimento.isEseguito())
            throw new MossaIllegaleException("Mossa illegale! Devi prima concludere il combattimento");

        int minimoArmate = 1;
        if (combattimento != null) {
            if (!statoPartenza.equals(combattimento.getStatoAttaccante()) ||
                    !statoArrivo.equals(combattimento.getStatoDifensore()))
                throw new MossaIllegaleException("Stati non corrispondenti ai territori del combattimento");
            minimoArmate = combattimento.getArmateAttaccante() - combattimento.getVittimeAttaccante();
        }

        if (spostamentoDTO.getArmate() < minimoArmate || spostamentoDTO.getArmate() >= statoPartenza.getArmate())
            throw new MossaIllegaleException("Numero armate non valido");

        // controlla che siano confinanti e dello stesso giocatore prima di eseguire lo spostamento
        statoPartenza.spostaArmate(statoArrivo, spostamentoDTO.getArmate());
        partita.setCombattimento(null);
        return giocatore.obRaggiunto();
    }

    /**
     * Un metodo che restituisce una carta territorio al giocatore di quel turno se ha conuquistato almeno uno stato
     * e che crea il nuovo turno.
     * @param partita l'oggetto partita al quale si riferisce fineTurno.
     * @return la carta territorio destinata al giocatore.
     */
    public CartaTerritorioDTO fineTurno(Partita partita) {
        if (partita.isFasePreparazione())
            throw new MossaIllegaleException("Mossa illegale: si e' ancora in fase di preparazione");

        if (partita.getCombattimento() != null)
            throw new MossaIllegaleException("Mossa illegale: c'e' ancora un combattimento in corso");

        if (partita.getGiocatoreAttivo().getArmateDisponibili() != 0)
            throw new MossaIllegaleException("Mossa illegale: il giocatore attivo ha ancora truppe da posizionare");

        // pesca una carta territorio se conquistato
        CartaTerritorio cartaTerritorio = null;
        if (partita.getConquista()) {
            cartaTerritorio = partita.getMazzo().pescaCarta(partita.getGiocatoreAttivo());
        }

        partita.nuovoTurno();

        return cartaTerritorio != null ? new CartaTerritorioDTO(cartaTerritorio) : null;
    }
}