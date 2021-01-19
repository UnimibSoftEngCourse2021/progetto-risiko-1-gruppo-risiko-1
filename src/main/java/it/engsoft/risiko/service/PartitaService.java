package it.engsoft.risiko.service;

import it.engsoft.risiko.dao.*;
import it.engsoft.risiko.dto.*;
import it.engsoft.risiko.exceptions.*;
import it.engsoft.risiko.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PartitaService {
    private final MappaService mappaService;
    private final CarteTerritorioService carteTerritorioService;
    private final CarteObiettivoService carteObiettivoService;

    @Autowired
    public PartitaService( MappaService mappaService, CarteObiettivoService carteObiettivoService,
                          CarteTerritorioService carteTerritorioService) {
        this.mappaService = mappaService;
        this.carteTerritorioService = carteTerritorioService;
        this.carteObiettivoService = carteObiettivoService;
    }

    public Partita nuovoGioco(NuovoGiocoDTO nuovoGiocoDTO) {
        Partita partita = new Partita();

        // setta la modalità
        partita.setModalita(Partita.Modalita.valutaModalita(nuovoGiocoDTO.getMod()));

        // istanzia la mappa
        Mappa mappa = mappaService.getMappa(nuovoGiocoDTO.getMappaId(), nuovoGiocoDTO.getMod());

        partita.setMappa(mappa);

        // controllo sul valore minimo e massimo di giocatori ammessi dalla mappa
        if (partita.getMappa().getNumMaxGiocatori() < nuovoGiocoDTO.getGiocatori().size() ||
                partita.getMappa().getNumMinGiocatori() > nuovoGiocoDTO.getGiocatori().size())
            throw new DatiErratiException("Dati errati: valore minimo/massimo di giocatori non ammesso");

        // creazione dei nuovi giocatori
        partita.setGiocatori(nuovoGiocoDTO.getGiocatori()
                .stream()
                .map(Giocatore::new)
                .collect(Collectors.toList()));

        // assegna gli obiettivi
        this.carteObiettivoService.setObiettiviGiocatori(partita.getMappa(), partita.getGiocatori(), nuovoGiocoDTO.isUnicoObiettivo());

        // scegliamo casualmente un ordine di giocatori
        Collections.shuffle(partita.getGiocatori());

        // distribuisci le carte territorio, comprende assegnazione degli stati
        this.carteTerritorioService.distribuisciCarte(partita);

        // inverte la lista dei giocatori
        Collections.reverse(partita.getGiocatori());

        partita.assegnaArmateIniziali();

        // metti un armata su ogni territorio e aggiorna quelle dei giocatori rispettivamente
        partita.getGiocatori().forEach(
                giocatore -> giocatore.getStati().forEach(
                        stato -> {
                            stato.aggiungiArmate(1);
                            giocatore.modificaTruppeDisponibili(-1);
                        }
                )
        )
        ;

        partita.setFasePreparazione(true);

        // viene impostato manualmente solo la prima volta, il primo della lista randomizzata
        partita.setGiocatoreAttivo(partita.getGiocatori().get(0));

        return partita;
    }

    public IniziaTurnoDAO iniziaTurno(Partita partita) {
        if (partita.isFasePreparazione())
            throw new MossaIllegaleException("Mossa illegale: impossibile iniziare il turno in fase di preparazione");

        // blocca iniziaTurno se fase diversa da NULL
        if (!partita.getTurno().getFase().equals(Turno.Fase.NULL))
            throw new MossaIllegaleException("Mossa illegale: fase del turno incorretta");
        // calcolo armate bonus date al giocatore attivo dagli stati conquistati
        int armateStati = Math.max(1, partita.getGiocatoreAttivo().getStati().size() / 3);

        // calcolo armate bonus date al giocatore attivo dai continenti conquistati
        int armateContinenti = 0;
        for (Continente continente : partita.getMappa().getContinenti()) {
            if (partita.getGiocatoreAttivo().equals(continente.getProprietario()))
                armateContinenti = armateContinenti + continente.getArmateBonus();
        }

        partita.getGiocatoreAttivo().modificaTruppeDisponibili(armateContinenti + armateStati);
        partita.getTurno().setFase(Turno.Fase.RINFORZI);
        return new IniziaTurnoDAO(partita.getTurno().getNumero(), partita.getGiocatoreAttivo().getNome(), armateStati, armateContinenti);
    }

    public RinforzoDAO rinforzo(RinforzoDTO rinforzoDTO, Partita partita) {

        // blocca il rinforzo se non chiamato dal giocatore attivo in quel turno
        Giocatore giocatore = toGiocatore(rinforzoDTO.getGiocatore(), partita);
        if (!partita.getGiocatoreAttivo().equals(giocatore))
            throw new MossaIllegaleException("Mossa illegale: rinforzo chiamato da un giocatore non attivo");

        if (partita.isFasePreparazione()) {
            int armateDaPiazzare = Math.min(3, giocatore.getTruppeDisponibili());
            eseguiRinforzo(rinforzoDTO, armateDaPiazzare, partita);

            // imposta il prossimo giocatore attivo
            partita.setProssimoGiocatoreAttivo();

            // se il prossimo giocatore non ha armate da piazzare allora la preparazione è finita: inizia la partita
            if (partita.getGiocatoreAttivo().getTruppeDisponibili() == 0) {
                partita.setFasePreparazione(false);
                partita.iniziaPrimoTurno();
            }

        } else { // è un rinforzo di inizio turno
            // blocca il rinforzo se non si è in fase di rinforzi
            if (!partita.getTurno().getFase().equals(Turno.Fase.RINFORZI))
                throw new MossaIllegaleException("Mossa illegale: rinforzo illegale se non in fase di rinforzi");

            // blocca il rinforzo se non ha armate da piazzare ( cioè è già stato fatto )
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
        int totale = 0;
        // controlla che i rinforzi totali siano quanto richiesto
        for (Long key : rinforzoDTO.getRinforzi().keySet()) {
            totale = totale + rinforzoDTO.getRinforzi().get(key);

            // controlla che ogni stato sia del giocatore attivo
            if (!toStato(key, partita).getProprietario().equals(partita.getGiocatoreAttivo()))
                throw new MossaIllegaleException("Mossa illegale: almeno uno degli stati non appartiene al giocatore attivo");
        }

        if (totale != armateDaPiazzare)
            throw new DatiErratiException("Dati errati: numero armate da piazzare non valido");

        // crea ed esegue un nuovo rinforzo per ogni coppia idStato-numeroArmate della mappa in ingresso
        for (Long key : rinforzoDTO.getRinforzi().keySet()) {
            Rinforzo rinforzo = new Rinforzo(toStato(key, partita), rinforzoDTO.getRinforzi().get(key));
            rinforzo.esegui();
        }

        partita.getGiocatoreAttivo().setTruppeDisponibili(0);
    }

    public int giocaTris(TrisDTO trisDTO, Partita partita) {
        // blocca giocaTris se si è in fase di preparazione
        if (partita.isFasePreparazione())
            throw new MossaIllegaleException("Mossa illegale: impossibile giocare un tris in fase di preparazione");

        // blocca giocaTris se il turno non è' in fase di rinforzo
        if (!partita.getTurno().getFase().equals(Turno.Fase.RINFORZI))
            throw new MossaIllegaleException("Mossa illegale: impossibile giocare un tris in fase di rinforzo");

        // blocca giocaTris se in trisDTO non sono contenute esattamente tre carte
        if (trisDTO.getTris().size() != 3)
            throw new DatiErratiException("Dati errati: passato un numero di carte diverso da 3");

        // seleziona giocatore che gioca il tris
        Giocatore giocatore = toGiocatore(trisDTO.getGiocatore(), partita);

        // blocca giocaTris se non viene chiamata dal giocatore attivo in quel turno
        if (!partita.getGiocatoreAttivo().equals(giocatore))
            throw new MossaIllegaleException("Mossa illegale: gioca tris chiamato da giocatore non attivo");

        int nArmateBonus = carteTerritorioService.valutaTris(partita.getMazzo(), trisDTO.getTris(), giocatore);
        partita.getGiocatoreAttivo().modificaTruppeDisponibili(nArmateBonus);

        return nArmateBonus;
    }

    public void attacco(AttaccoDTO attaccoDTO, Partita partita) {
        // blocca l'attacco se si è in fase di preparazione
        if (partita.isFasePreparazione())
            throw new MossaIllegaleException("Mossa illegale: impossibile attaccare in fase di preparazione");

        // blocca l'attacco se si è in fase di spostamento
        if (partita.getTurno().getFase().equals(Turno.Fase.SPOSTAMENTO))
            throw new MossaIllegaleException("Mossa illegale: impossibile attaccare in fase di sopstamento");

        // blocca l'attacco se il giocatore attivo ha ancora truppe da posizionare
        if (partita.getGiocatoreAttivo().getTruppeDisponibili() != 0)
            throw new MossaIllegaleException("Mossa illegale: impossibile attaccare se ci sono ancora truppe da posizionare");

        // blocca l'attacco se c'e' un combattimento in corso
        if (partita.getTurno().getCombattimentoInCorso() != null)
            throw new MossaIllegaleException("Mossa illegale: impossibile attaccare durante un combattimento gia' in corso");

        Giocatore attaccante = toGiocatore(attaccoDTO.getGiocatore(), partita);
        Stato statoAttaccante = toStato(attaccoDTO.getAttaccante(), partita);
        Stato statoDifensore = toStato(attaccoDTO.getDifensore(), partita);

        // blocca l'attacco se non viene chiamato dal giocatore attivo in quel turno
        if (!partita.getGiocatoreAttivo().equals(attaccante))
            throw new MossaIllegaleException("Mossa illegale: impossibile attaccare per un giocatore non attivo");

        // blocca l'attacco se lo stato attaccante non appartiene al giocatore attivo
        if (!attaccante.equals(statoAttaccante.getProprietario()))
            throw new MossaIllegaleException("Mossa illegale: lo stato attaccante non appartiene al giocatore attivo");

        // blocca l'attacco se cerca di attaccare un proprio territorio
        if (partita.getGiocatoreAttivo().equals(statoDifensore.getProprietario()))
            throw new MossaIllegaleException("Mossa illegale: impossibile attaccare un proprio territorio");

        partita.getTurno().setFase(Turno.Fase.COMBATTIMENTI);
        partita.getTurno().setCombattimentoInCorso(
                new Combattimento(statoAttaccante, statoDifensore, attaccoDTO.getArmate()));
    }

    public DifesaDAO difesa(DifesaDTO difesaDTO, Partita partita) {
        // blocca la difesa se si è in fase di preparazione
        if (partita.isFasePreparazione())
            throw new MossaIllegaleException("Mossa illegale: si e' in fase di preparazione");

        // blocca la difesa se il giocatore attivo ha ancora truppe da posizionare
        if (partita.getGiocatoreAttivo().getTruppeDisponibili() != 0)
            throw new MossaIllegaleException("Mossa illegale: il giocatore attivo ha ancora truppe da posizionare");

        // blocca la difesa se non c'e' un combattimento in corso
        if (partita.getTurno().getCombattimentoInCorso() == null)
            throw new MossaIllegaleException("Mossa illegale: non c'e' un combattimento in corso");

        // blocca la difesa se non si è in fase di combattimento
        if (!partita.getTurno().getFase().equals(Turno.Fase.COMBATTIMENTI))
            throw new MossaIllegaleException("Mossa illegale: non si e' in fase di combattimento");

        Stato statoAtt = partita.getTurno().getCombattimentoInCorso().getStatoAttaccante();
        Stato statoDif = partita.getTurno().getCombattimentoInCorso().getStatoDifensore();
        Giocatore giocatoreDif = partita.getTurno().getCombattimentoInCorso().getStatoDifensore().getProprietario();
        Giocatore giocatoreAtt = partita.getTurno().getCombattimentoInCorso().getStatoAttaccante().getProprietario();

        // blocca la difesa se chiamata da un giocatore non coinvolto come difensore nell'attuale combattimento
        if (!giocatoreDif.equals(toGiocatore(difesaDTO.getGiocatore(), partita)))
            throw new MossaIllegaleException("Mossa illegale: difesa chiamata da un giocatore non coinvolto come difensore nell'attuale combattimento");

        // esecuzione combattimento
        partita.getTurno().getCombattimentoInCorso().simulaCombattimento(difesaDTO.getArmate());

        // rimozione delle truppe ad attaccante e difensore in base all'esito del combattimento
        statoAtt.rimuoviArmate(
                partita.getTurno().getCombattimentoInCorso().getVittimeAttaccante());
        statoDif.rimuoviArmate(
                partita.getTurno().getCombattimentoInCorso().getVittimeDifensore());


        // se l'attaccante ha conquistato lo stato del difensore ne diventa il proprietario
        if (partita.getTurno().getCombattimentoInCorso().getConquista()) {
            statoDif.setProprietario(giocatoreAtt);
            giocatoreAtt.aggiungiStato(partita.getTurno().getCombattimentoInCorso().getStatoDifensore());
            giocatoreDif.rimuoviStato(partita.getTurno().getCombattimentoInCorso().getStatoDifensore());
            partita.getTurno().registraConquista(); // setta true conquista avvenuta in turno

            // gestione difensore eliminato
            if (giocatoreDif.isEliminato())
                giocatoreDif.setUccisore(giocatoreAtt);
        }

        // crea oggetto difesaDAO
        DifesaDAO difesaDAO = new DifesaDAO(
                partita.getTurno().getCombattimentoInCorso(),
                giocatoreAtt.obRaggiunto(),
                giocatoreDif.isEliminato());

        // se l'attaccante non ha conquistato il territorio, il combattimento viene messo a null
        if (!partita.getTurno().getCombattimentoInCorso().getConquista())
            partita.getTurno().setCombattimentoInCorso(null);


        return difesaDAO;
    }

    public boolean spostamentoStrategico(SpostamentoDTO spostamentoDTO, Partita partita) {
        // blocca lo spostamento se si è in fase di preparazione
        if (partita.isFasePreparazione())
            throw new MossaIllegaleException("Mossa illegale: si e' ancora in fase di preparazione");

        Giocatore giocatore = toGiocatore(spostamentoDTO.getGiocatore(), partita);
        Stato statoArrivo = toStato(spostamentoDTO.getStatoArrivo(), partita);
        Stato statoPartenza = toStato(spostamentoDTO.getStatoPartenza(), partita);

        // blocca lo spostamento se non viene chiamato dal giocatore attivo in quel turno
        if (!partita.getGiocatoreAttivo().equals(giocatore))
            throw new MossaIllegaleException("Mossa illegale: spostamento non chiamato dal giocatore attivo");

        // blocca lo spostamento se lo stato di partenza e di arrivo non appartengono allo stesso giocatore
        if (!statoArrivo.getProprietario().equals(statoPartenza.getProprietario()))
            throw new MossaIllegaleException("Mossa illegale: stato di partenza e di arrivo non appartengono allo stesso giocatore");

        if (partita.getTurno().getCombattimentoInCorso() != null) {
            SpostamentoStrategico spostamentoAttacco =
                    new SpostamentoStrategico(statoPartenza, statoArrivo, spostamentoDTO.getArmate());
            if (armateNonValide(spostamentoAttacco, partita))
                throw new DatiErratiException("Dati errati: armate spostamentoAttacco non valide");
            spostamentoAttacco.esegui();
            partita.getTurno().setCombattimentoInCorso(null);
        } else {
            partita.getTurno().setFase(Turno.Fase.SPOSTAMENTO);
            SpostamentoStrategico spostamentoStrategico =
                    new SpostamentoStrategico(statoPartenza, statoArrivo, spostamentoDTO.getArmate());
            if (armateNonValide(spostamentoStrategico, partita))
                throw new DatiErratiException("Dati errati: armate spostamento non valide");
            spostamentoStrategico.esegui();
        }

        return giocatore.obRaggiunto();
    }

    public CartaTerritorioDAO fineTurno(Partita partita) {
        // blocca fineTurno se si è in fase di preparazione
        if (partita.isFasePreparazione())
            throw new MossaIllegaleException("Mossa illegale: si e' ancora in fase di preparazione");

        // blocca fineTurno se c'è un combattimento in corso
        if (partita.getTurno().getCombattimentoInCorso() != null)
            throw new MossaIllegaleException("Mossa illegale: c'e' ancora un combattimento in corso");

        // blocca fineTurno se il giocatore attivo ha ancora truppe da posizionare
        if (partita.getGiocatoreAttivo().getTruppeDisponibili() != 0)
            throw new MossaIllegaleException("Mossa illegale: il giocatore attivo ha ancora truppe da posizionare");

        // pesca una carta territorio se conquistato
        CartaTerritorio cartaTerritorio = null;
        if (partita.getTurno().conquistaAvvenuta()) {
            cartaTerritorio = carteTerritorioService.pescaCarta(partita.getMazzo(), partita.getGiocatoreAttivo());
        }

        partita.nuovoTurno();

        return cartaTerritorio != null ? new CartaTerritorioDAO(cartaTerritorio) : null;
    }


    private Stato toStato(Long idStato, Partita partita) {
        List<Stato> stato;
        stato = partita.getMappa().getStati().stream().filter(
                s -> s.getId().equals(idStato)
        ).collect(Collectors.toList());
        if (stato.get(0) == null)
            throw new DatiErratiException("Dati errati: stato inesistente");
        return stato.get(0);
    }

    private Giocatore toGiocatore(String nomeGiocatore, Partita partita) {
        Giocatore giocatore = null;
        for (int i = 0; i < partita.getGiocatori().size(); i++) {
            if (nomeGiocatore.equals(partita.getGiocatori().get(i).getNome()))
                giocatore = partita.getGiocatori().get(i);
        }
        if (giocatore == null)
            throw new DatiErratiException("Dati errati: giocatore inesistente");
        return giocatore;
    }

    private boolean armateNonValide(SpostamentoStrategico spostamentoStrategico, Partita partita) {
        if (partita.getTurno().getCombattimentoInCorso() != null)
            return spostamentoStrategico.getQuantita() <= 0
                    ||
                    spostamentoStrategico.getQuantita() <=
                            partita.getTurno().getCombattimentoInCorso().getArmateAttaccante() -
                                    partita.getTurno().getCombattimentoInCorso().getVittimeAttaccante() - 1
                    ||
                    spostamentoStrategico.getQuantita() >= spostamentoStrategico.getPartenza().getArmate()
                    ;

        return spostamentoStrategico.getQuantita() <= 0
                ||
                spostamentoStrategico.getQuantita() >= spostamentoStrategico.getPartenza().getArmate()
                ;
    }
}