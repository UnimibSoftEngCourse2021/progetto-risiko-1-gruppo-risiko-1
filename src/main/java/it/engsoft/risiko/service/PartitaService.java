package it.engsoft.risiko.service;

import it.engsoft.risiko.rest.dao.*;
import it.engsoft.risiko.data.creators.ObiettivoFactory;
import it.engsoft.risiko.rest.dto.*;
import it.engsoft.risiko.exceptions.*;
import it.engsoft.risiko.data.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PartitaService {
    private final MappaService mappaService;

    @Autowired
    public PartitaService( MappaService mappaService) {
        this.mappaService = mappaService;
    }

    public Partita nuovoGioco(NuovoGiocoDTO nuovoGiocoDTO) {
        Modalita modalita = Modalita.valutaModalita(nuovoGiocoDTO.getMod());

        // istanzia la mappa
        Mappa mappa = mappaService.getMappa(nuovoGiocoDTO.getMappaId(), modalita);

        // controllo sul valore minimo e massimo di giocatori ammessi dalla mappa
        if (mappa.getNumMaxGiocatori() < nuovoGiocoDTO.getGiocatori().size() ||
                mappa.getNumMinGiocatori() > nuovoGiocoDTO.getGiocatori().size())
            throw new DatiErratiException("Dati errati: valore minimo/massimo di giocatori non ammesso");

        // creazione dei nuovi giocatori
        List<Giocatore> giocatori = nuovoGiocoDTO.getGiocatori()
                .stream()
                .map(Giocatore::new)
                .collect(Collectors.toList());

        // assegna gli obiettivi ai giocatori
        ObiettivoFactory obFactory = new ObiettivoFactory(mappa, giocatori, nuovoGiocoDTO.isUnicoObiettivo());
        giocatori.forEach(g -> g.setObiettivo(obFactory.getNuovoObiettivo()));

        // scegliamo casualmente un ordine di giocatori
        Collections.shuffle(giocatori);

        Partita partita = new Partita(mappa, giocatori, modalita);

        // distribuisci le carte territorio, comprende assegnazione degli stati
        partita.getMazzo().distribuisciCarte(partita);

        partita.occupazioneInizialeTerritori();

        return partita;
    }

    public IniziaTurnoDAO iniziaTurno(Partita partita) {
        if (partita.isFasePreparazione())
            throw new MossaIllegaleException("Mossa illegale: impossibile iniziare il turno in fase di preparazione");

        if (!partita.getTurno().getFase().equals(Turno.Fase.INIZIALIZZAZIONE))
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
        partita.getTurno().setFase(Turno.Fase.RINFORZI);
        return new IniziaTurnoDAO(partita.getTurno().getNumero(), giocatore.getNome(), armateStati, armateContinenti);
    }

    public RinforzoDAO rinforzo(RinforzoDTO rinforzoDTO, Partita partita) {
        Giocatore giocatore = partita.getGiocatoreAttivo();
        if (!giocatore.getNome().equals(rinforzoDTO.getGiocatore()))
            throw new MossaIllegaleException("Mossa illegale: rinforzo non chiamato dal giocatore attivo");

        if (partita.isFasePreparazione()) {
            int armateDaPiazzare = Math.min(3, giocatore.getTruppeDisponibili());
            eseguiRinforzo(rinforzoDTO, armateDaPiazzare, partita);

            partita.setNuovoGiocatoreAttivoPreparazione();

        } else { // è un rinforzo di inizio turno
            if (!partita.getTurno().getFase().equals(Turno.Fase.RINFORZI))
                throw new MossaIllegaleException("Mossa illegale: rinforzo illegale se non in fase di rinforzi");

            if (giocatore.getTruppeDisponibili() == 0)
                throw new MossaIllegaleException("Mossa illegale: non ci sono altre armate da piazzare");

            eseguiRinforzo(rinforzoDTO, partita.getGiocatoreAttivo().getTruppeDisponibili(), partita);
        }

        return new RinforzoDAO(
                partita.getGiocatoreAttivo().getNome(),
                partita.isFasePreparazione(),
                partita.getGiocatoreAttivo().obRaggiunto()
        );
    }

    private void eseguiRinforzo(RinforzoDTO rinforzoDTO, int armateDaPiazzare, Partita partita) {
        Giocatore giocAttivo = partita.getGiocatoreAttivo();
        Mappa mappa = partita.getMappa();
        int totale = 0;
        // controlla che i rinforzi totali siano quanto richiesto
        for (Long key : rinforzoDTO.getRinforzi().keySet()) {
            totale = totale + rinforzoDTO.getRinforzi().get(key);

            // controlla che ogni stato sia del giocatore attivo
            if (!mappa.getStatoById(key).getProprietario().equals(giocAttivo))
                throw new MossaIllegaleException("Mossa illegale: almeno uno degli stati non appartiene al giocatore attivo");
        }

        if (totale != armateDaPiazzare)
            throw new DatiErratiException("Dati errati: numero armate da piazzare non valido");

        // crea ed esegue un nuovo rinforzo per ogni coppia idStato-numeroArmate della mappa in ingresso
        for (Long key : rinforzoDTO.getRinforzi().keySet()) {
            Stato stato = mappa.getStatoById(key);
            stato.aggiungiArmate(rinforzoDTO.getRinforzi().get(key));
        }

        giocAttivo.modificaTruppeDisponibili(-armateDaPiazzare);
    }

    public int giocaTris(TrisDTO trisDTO, Partita partita) {
        if (partita.isFasePreparazione())
            throw new MossaIllegaleException("Mossa illegale: impossibile giocare un tris in fase di preparazione");

        if (!partita.getTurno().getFase().equals(Turno.Fase.RINFORZI))
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

    public void attacco(AttaccoDTO attaccoDTO, Partita partita) {
        if (partita.isFasePreparazione() || partita.getTurno().getFase().equals(Turno.Fase.SPOSTAMENTO))
            throw new MossaIllegaleException("Mossa illegale: impossibile attaccare in questa fase di gioco");

        if (partita.getTurno().getCombattimentoInCorso() != null)
            throw new MossaIllegaleException("Mossa illegale: un combattimento è già in corso");

        Giocatore giocatoreAtt = partita.getGiocatoreAttivo();

        if (giocatoreAtt.getTruppeDisponibili() != 0)
            throw new MossaIllegaleException("Mossa illegale: impossibile attaccare prima di aver completato i rinforzi");

        if (!giocatoreAtt.getNome().equals(attaccoDTO.getGiocatore()))
            throw new MossaIllegaleException("Mossa illegale: non è il tuo turno");

        Stato statoAttaccante = partita.getMappa().getStatoById(attaccoDTO.getAttaccante());
        Stato statoDifensore = partita.getMappa().getStatoById(attaccoDTO.getDifensore());

        if (!giocatoreAtt.equals(statoAttaccante.getProprietario()))
            throw new MossaIllegaleException("Mossa illegale: lo stato attaccante non appartiene al giocatore attivo");

        // il costruttore verifica che gli stati siano compatibili per un attacco e che il n di armate sia valido
        Combattimento combattimento = new Combattimento(statoAttaccante, statoDifensore, attaccoDTO.getArmate());
        partita.getTurno().setFase(Turno.Fase.COMBATTIMENTI);
        partita.getTurno().setCombattimentoInCorso(combattimento);
    }

    public DifesaDAO difesa(DifesaDTO difesaDTO, Partita partita) {
        Combattimento combattimento = partita.getTurno().getCombattimentoInCorso();
        if (combattimento == null || combattimento.isEseguito())
            throw new MossaIllegaleException("Mossa illegale: non c'e' un combattimento in corso");

        Giocatore giocatoreDif = combattimento.getStatoDifensore().getProprietario();
        Giocatore giocatoreAtt = combattimento.getStatoAttaccante().getProprietario();

        if (!giocatoreDif.getNome().equals(difesaDTO.getGiocatore()))
            throw new MossaIllegaleException("Mossa illegale: giocatore difensore non valido");

        combattimento.esegui(difesaDTO.getArmate());

        if (combattimento.getConquista()) {
            partita.getTurno().registraConquista(); // setta true conquista avvenuta in turno

            // gestione difensore eliminato
            if (giocatoreDif.isEliminato())
                giocatoreDif.setUccisore(giocatoreAtt);
        }

        // se l'attaccante non ha conquistato il territorio, il combattimento è terminato
        if (!combattimento.getConquista())
            partita.getTurno().setCombattimentoInCorso(null);

        return new DifesaDAO(
                combattimento,
                giocatoreAtt.obRaggiunto(),
                giocatoreDif.isEliminato());
    }

    public boolean spostamentoStrategico(SpostamentoDTO spostamentoDTO, Partita partita) {
        if (partita.isFasePreparazione())
            throw new MossaIllegaleException("Mossa illegale: si e' ancora in fase di preparazione");

        Giocatore giocatore = partita.getGiocatoreAttivo();
        Stato statoArrivo = partita.getMappa().getStatoById(spostamentoDTO.getStatoArrivo());
        Stato statoPartenza = partita.getMappa().getStatoById(spostamentoDTO.getStatoPartenza());

        if (!giocatore.getNome().equals(spostamentoDTO.getGiocatore()))
            throw new MossaIllegaleException("Mossa illegale: spostamento non chiamato dal giocatore attivo");

        Combattimento combattimento = partita.getTurno().getCombattimentoInCorso();

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
        partita.getTurno().setCombattimentoInCorso(null);
        return giocatore.obRaggiunto();
    }

    public CartaTerritorioDAO fineTurno(Partita partita) {
        if (partita.isFasePreparazione())
            throw new MossaIllegaleException("Mossa illegale: si e' ancora in fase di preparazione");

        if (partita.getTurno().getCombattimentoInCorso() != null)
            throw new MossaIllegaleException("Mossa illegale: c'e' ancora un combattimento in corso");

        if (partita.getGiocatoreAttivo().getTruppeDisponibili() != 0)
            throw new MossaIllegaleException("Mossa illegale: il giocatore attivo ha ancora truppe da posizionare");

        // pesca una carta territorio se conquistato
        CartaTerritorio cartaTerritorio = null;
        if (partita.getTurno().conquistaAvvenuta()) {
            cartaTerritorio = partita.getMazzo().pescaCarta(partita.getGiocatoreAttivo());
        }

        partita.nuovoTurno();

        return cartaTerritorio != null ? new CartaTerritorioDAO(cartaTerritorio) : null;
    }
}