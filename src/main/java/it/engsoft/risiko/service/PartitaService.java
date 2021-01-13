package it.engsoft.risiko.service;

import it.engsoft.risiko.dao.*;
import it.engsoft.risiko.dto.*;
import it.engsoft.risiko.exceptions.*;
import it.engsoft.risiko.model.*;
import it.engsoft.risiko.repository.MappaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PartitaService {
    private final MappaRepository mappaRepository;
    private final CarteTerritorioService carteTerritorioService;
    private final CarteObiettivoService carteObiettivoService;

    @Autowired
    public PartitaService(MappaRepository mappaRepository, CarteObiettivoService carteObiettivoService,
                          CarteTerritorioService carteTerritorioService) {
        this.mappaRepository = mappaRepository;
        this.carteTerritorioService = carteTerritorioService;
        this.carteObiettivoService = carteObiettivoService;
    }

    public Partita nuovoGioco(NuovoGiocoDTO nuovoGiocoDTO) {
        Partita partita = new Partita();

        // istanzia la mappa caricandola tramite id da un repository
        Optional<Mappa> mappa = mappaRepository.findById(nuovoGiocoDTO.getMappaId());
        if (mappa.isEmpty())
            throw new DatiErratiException();

        partita.setMappa(mappa.get());

        // controllo sul valore minimo e massimo di giocatori ammessi dalla mappa
        if (partita.getMappa().getNumMaxGiocatori() < nuovoGiocoDTO.getGiocatori().size() ||
                partita.getMappa().getNumMinGiocatori() > nuovoGiocoDTO.getGiocatori().size())
            throw new DatiErratiException();

        // creazione dei nuovi giocatori
        partita.setGiocatori(nuovoGiocoDTO.getGiocatori()
                .stream()
                .map(Giocatore::new)
                .collect(Collectors.toList()));

        // assegna gli obiettivi
        this.carteObiettivoService.setObiettiviGiocatori(partita.getMappa(), partita.getGiocatori());

        // distribuisci le carte territorio, comprende assegnazione degli stati
        this.carteTerritorioService.distribuisciCarte(partita);

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

        // setta la modalità
        partita.setModalita(Partita.Modalita.valutaModalita(nuovoGiocoDTO.getMod()));

        partita.setFasePreparazione(true);

        //TODO:: integrazione compattazione mappa in mappa service

        // scegliamo casualmente un ordine di giocatori
        Collections.shuffle(partita.getGiocatori());

        // viene impostato manualmente solo la prima volta, il primo della lista randomizzata
        partita.setGiocatoreAttivo(partita.getGiocatori().get(0));

        return partita;
    }

    public IniziaTurnoDAO iniziaTurno(Partita partita) {
        if (partita.isFasePreparazione())
            throw new MossaIllegaleException();

        // blocca iniziaTurno se fase diversa da NULL
        if (!partita.getTurno().getFase().equals(Turno.Fase.NULL))
            throw new MossaIllegaleException();

        // calcolo armate bonus date al giocatore attivo dagli stati conquistati
        int armateStati = partita.getGiocatoreAttivo().getStati().size() / 3;

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

    public Map<String, Object> rinforzo(RinforzoDTO rinforzoDTO, Partita partita) {

        // blocca il rinforzo se non chiamato dal giocatore attivo in quel turno
        Giocatore giocatore = toGiocatore(rinforzoDTO.getGiocatore(), partita);
        if (!partita.getGiocatoreAttivo().equals(giocatore))
            throw new MossaIllegaleException();

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
                throw new MossaIllegaleException();

            // blocca il rinforzo se non ha armate da piazzare ( cioè è già stato fatto )
            if (giocatore.getTruppeDisponibili() == 0)
                throw new MossaIllegaleException();

            eseguiRinforzo(rinforzoDTO, partita.getGiocatoreAttivo().getTruppeDisponibili(), partita);
        }

        Map<String, Object> risposta = new HashMap<>();
        risposta.put("giocatore", partita.getGiocatoreAttivo().getNome());
        risposta.put("preparazione", partita.isFasePreparazione());

        return risposta;
    }

    private void eseguiRinforzo(RinforzoDTO rinforzoDTO, int armateDaPiazzare, Partita partita) {
        int totale = 0;
        // controlla che i rinforzi totali siano quanto richiesto
        for (Long key : rinforzoDTO.getRinforzi().keySet()) {
            totale = totale + rinforzoDTO.getRinforzi().get(key);

            // controlla che ogni stato sia del giocatore attivo
            if (!toStato(key, partita).getProprietario().equals(partita.getGiocatoreAttivo()))
                throw new MossaIllegaleException();
        }

        if (totale != armateDaPiazzare)
            throw new MossaIllegaleException();

        // crea ed esegue un nuovo rinforzo per ogni coppia idStato-numeroArmate della mappa in ingresso
        for (Long key : rinforzoDTO.getRinforzi().keySet()) {
            Rinforzo rinforzo = new Rinforzo(toStato(key, partita), rinforzoDTO.getRinforzi().get(key));
            rinforzo.esegui();
        }

        partita.getGiocatoreAttivo().modificaTruppeDisponibili(-armateDaPiazzare);
    }

    public int giocaTris(TrisDTO trisDTO, Partita partita) {
        // blocca giocaTris se si è in fase di preparazione
        if (partita.isFasePreparazione())
            throw new MossaIllegaleException();

        // blocca giocaTris se il turno non è' in fase di rinforzo
        if (!partita.getTurno().getFase().equals(Turno.Fase.RINFORZI))
            throw new MossaIllegaleException();

        // blocca giocaTris se in trisDTO non sono contenute esattamente tre carte
        if (trisDTO.getTris().size() != 3)
            throw new DatiErratiException();

        // seleziona giocatore che gioca il tris
        Giocatore giocatore = toGiocatore(trisDTO.getGiocatore(), partita);

        // blocca giocaTris se non viene chiamata dal giocatore attivo in quel turno
        if (!partita.getGiocatoreAttivo().equals(giocatore))
            throw new MossaIllegaleException();

        int nArmateBonus = carteTerritorioService.valutaTris(partita.getMazzo(), trisDTO.getTris(), giocatore);
        partita.getGiocatoreAttivo().modificaTruppeDisponibili(nArmateBonus);

        return nArmateBonus;
    }

    public void attacco(AttaccoDTO attaccoDTO, Partita partita) {
        // blocca l'attacco se si è in fase di preparazione
        if (partita.isFasePreparazione())
            throw new MossaIllegaleException();

        // blocca l'attacco se si è in fase di spostamento
        if (partita.getTurno().getFase().equals(Turno.Fase.SPOSTAMENTO))
            throw new MossaIllegaleException();

        // blocca l'attacco se il giocatore attivo ha ancora truppe da posizionare
        if (partita.getGiocatoreAttivo().getTruppeDisponibili() != 0)
            throw new MossaIllegaleException();

        // blocca l'attacco se c'e' un combattimento in corso
        if (partita.getTurno().getCombattimentoInCorso() != null)
            throw new MossaIllegaleException();

        Giocatore attaccante = toGiocatore(attaccoDTO.getGiocatore(), partita);
        Stato statoAttaccante = toStato(attaccoDTO.getAttaccante(), partita);
        Stato statoDifensore = toStato(attaccoDTO.getDifensore(), partita);

        // blocca l'attacco se non viene chiamato dal giocatore attivo in quel turno
        if (!partita.getGiocatoreAttivo().equals(attaccante))
            throw new MossaIllegaleException();

        // blocca l'attacco se lo stato attaccante non appartiene al giocatore attivo
        if (!attaccante.equals(statoAttaccante.getProprietario()))
            throw new MossaIllegaleException();

        // blocca l'attacco se cerca di attaccare un proprio territorio
        if (partita.getGiocatoreAttivo().equals(statoDifensore.getProprietario()))
            throw new MossaIllegaleException();

        partita.getTurno().setFase(Turno.Fase.COMBATTIMENTI);
        partita.getTurno().setCombattimentoInCorso(
                new Combattimento(statoAttaccante, statoDifensore, attaccoDTO.getArmate()));
    }

    public DifesaDAO difesa(DifesaDTO difesaDTO, Partita partita) {
        // blocca la difesa se si è in fase di preparazione
        if (partita.isFasePreparazione())
            throw new MossaIllegaleException();

        // blocca la difesa se il giocatore attivo ha ancora truppe da posizionare
        if (partita.getGiocatoreAttivo().getTruppeDisponibili() != 0)
            throw new MossaIllegaleException();

        // blocca la difesa se non c'e' un combattimento in corso
        if (partita.getTurno().getCombattimentoInCorso() == null)
            throw new MossaIllegaleException();

        // blocca la difesa se non si è in fase di combattimento
        if (!partita.getTurno().getFase().equals(Turno.Fase.COMBATTIMENTI))
            throw new MossaIllegaleException();

        Stato statoAtt = partita.getTurno().getCombattimentoInCorso().getStatoAttaccante();
        Stato statoDif = partita.getTurno().getCombattimentoInCorso().getStatoDifensore();
        Giocatore giocatoreDif = partita.getTurno().getCombattimentoInCorso().getStatoDifensore().getProprietario();
        Giocatore giocatoreAtt = partita.getTurno().getCombattimentoInCorso().getStatoAttaccante().getProprietario();

        // blocca la difesa se chiamata da un giocatore non coinvolto come difensore nell'attuale combattimento
        if (!giocatoreDif.equals(toGiocatore(difesaDTO.getGiocatore(), partita)))
            throw new MossaIllegaleException();

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

    public void spostamentoStrategico(SpostamentoDTO spostamentoDTO, Partita partita) {
        // blocca lo spostamento se si è in fase di preparazione
        if (partita.isFasePreparazione())
            throw new MossaIllegaleException();

        Giocatore giocatore = toGiocatore(spostamentoDTO.getGiocatore(), partita);
        Stato statoArrivo = toStato(spostamentoDTO.getStatoArrivo(), partita);
        Stato statoPartenza = toStato(spostamentoDTO.getStatoPartenza(), partita);

        // blocca lo spostamento se non viene chiamato dal giocatore attivo in quel turno
        if (!partita.getGiocatoreAttivo().equals(giocatore))
            throw new MossaIllegaleException();

        // blocca lo spostamento se lo stato di partenza e di arrivo non appartengono allo stesso giocatore
        if (!statoArrivo.getProprietario().equals(statoPartenza.getProprietario()))
            throw new MossaIllegaleException();

        if (partita.getTurno().getCombattimentoInCorso() != null) {
            SpostamentoStrategico spostamentoAttacco =
                    new SpostamentoStrategico(statoPartenza, statoArrivo, spostamentoDTO.getArmate());
            if (armateNonValide(spostamentoAttacco, partita))
                throw new DatiErratiException();
            spostamentoAttacco.esegui();
            partita.getTurno().setCombattimentoInCorso(null);
        } else {
            partita.getTurno().setFase(Turno.Fase.SPOSTAMENTO);
            SpostamentoStrategico spostamentoStrategico =
                    new SpostamentoStrategico(statoPartenza, statoArrivo, spostamentoDTO.getArmate());
            if (armateNonValide(spostamentoStrategico, partita))
                throw new DatiErratiException();
            spostamentoStrategico.esegui();
        }
    }

    public CartaTerritorioDAO fineTurno(Partita partita) {
        // blocca fineTurno se si è in fase di preparazione
        if (partita.isFasePreparazione())
            throw new MossaIllegaleException();

        // blocca fineTurno se c'è un combattimento in corso
        if (partita.getTurno().getCombattimentoInCorso() != null)
            throw new MossaIllegaleException();

        // blocca fineTurno se il giocatore attivo ha ancora truppe da posizionare
        if (partita.getGiocatoreAttivo().getTruppeDisponibili() != 0)
            throw new MossaIllegaleException();

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
            throw new DatiErratiException();
        return stato.get(0);
    }

    private Giocatore toGiocatore(String nomeGiocatore, Partita partita) {
        Giocatore giocatore = null;
        for (int i = 0; i < partita.getGiocatori().size(); i++) {
            if (nomeGiocatore.equals(partita.getGiocatori().get(i).getNome()))
                giocatore = partita.getGiocatori().get(i);
        }
        if (giocatore == null)
            throw new DatiErratiException();
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