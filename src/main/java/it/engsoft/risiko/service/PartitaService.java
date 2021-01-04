package it.engsoft.risiko.service;

import it.engsoft.risiko.dao.CartaTerritorioDAO;
import it.engsoft.risiko.dao.DifesaDAO;
import it.engsoft.risiko.dao.IniziaTurnoDAO;
import it.engsoft.risiko.dao.NuovoGiocoDAO;
import it.engsoft.risiko.dto.*;
import it.engsoft.risiko.exceptions.MossaIllegaleException;
import it.engsoft.risiko.model.*;
import it.engsoft.risiko.repository.MappaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Scope(value = WebApplicationContext.SCOPE_SESSION)
public class PartitaService {
    private Partita partita;
    private Boolean fasePreparazione;
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

    public NuovoGiocoDAO nuovoGioco(NuovoGiocoDTO nuovoGiocoDTO) {
        this.partita = new Partita();

        // istanzia la mappa caricandola tramite id da un repository
        this.partita.setMappa(mappaRepository.findById(nuovoGiocoDTO.getMappaId()));

        // controllo sul valore minimo e massimo di giocatori ammessi dalla mappa
        if (this.partita.getMappa().getNumMaxGiocatori() < nuovoGiocoDTO.getGiocatori().size() ||
                this.partita.getMappa().getNumMinGiocatori() > nuovoGiocoDTO.getGiocatori().size())
            throw new RuntimeException("Ecceduto il limite massimo-minimo di giocatori");

        // creazione dei nuovi giocatori
        this.partita.setGiocatori(nuovoGiocoDTO.getGiocatori()
                .stream()
                .map(Giocatore::new)
                .collect(Collectors.toList()));

        // assegna gli obiettivi
        this.carteObiettivoService.setObiettiviGiocatori(partita.getMappa(), partita.getGiocatori());

        // distribuisci le carte territorio
        this.carteTerritorioService.distribuisciCarte(partita);

        // metti un armata su ogni territorio e aggiorna quelle dei giocatori rispettivamente
        partita.getGiocatori().forEach(giocatore -> {
            giocatore.getStati().forEach(stato -> {
                stato.aggiungiArmate(1);
                giocatore.setTruppeDisponibili(giocatore.getTruppeDisponibili() - 1);
            });
        });

        // setta la modalità
        if (nuovoGiocoDTO.getMod().equalsIgnoreCase(Partita.Modalita.LENTA.toString()))
            this.partita.setModalita(Partita.Modalita.LENTA);
        if (nuovoGiocoDTO.getMod().equalsIgnoreCase(Partita.Modalita.NORMALE.toString()))
            this.partita.setModalita(Partita.Modalita.NORMALE);
        if (nuovoGiocoDTO.getMod().equalsIgnoreCase(Partita.Modalita.VELOCE.toString()))
            this.partita.setModalita(Partita.Modalita.VELOCE);

        fasePreparazione = true;

        // scegliamo casualmente un ordine di giocatori
        Collections.shuffle(partita.getGiocatori());

        // viene impostato manualmente solo la prima volta, il primo della lista randomizzata
        partita.setGiocatoreAttivo(partita.getGiocatori().get(0));

        return new NuovoGiocoDAO(this.partita);
    }

    public IniziaTurnoDAO iniziaTurno() {
        // TODO: calcolare le armate bonus per questo turno e ritornarle
        return new IniziaTurnoDAO(this.partita.getTurno());
    }

    public void rinforzo(RinforzoDTO rinforzoDTO) {
        // gestione rinforzi iniziali, credo convenga aggiungere dei controlli sul numero di armate in input da lato client
        Giocatore giocatore = toGiocatore(rinforzoDTO.getGiocatore());
        if (!partita.getGiocatoreAttivo().equals(giocatore))
            throw new MossaIllegaleException();

        if (fasePreparazione) {
            int armateDaPiazzare = Math.min(3, giocatore.getTruppeDisponibili());
            eseguiRinforzo(rinforzoDTO, armateDaPiazzare);

            // imposta il prossimo giocatore attivo
            partita.setProssimoGiocatoreAttivo();

            // se il prossimo giocatore non ha armate da piazzare allora la preparazione è finita: inizia la partita
            if (partita.getGiocatoreAttivo().getTruppeDisponibili() == 0) {
                fasePreparazione = false;
                partita.iniziaPrimoTurno();
            }
        } else { // è un rinforzo di inizio turno
            // blocca il rinforzo se non si è in fase di rinforzi
            if (!this.partita.getTurno().getFase().equals(Turno.Fase.RINFORZI))
                throw new MossaIllegaleException();

            // blocca il rinforzo se non viene chiamato dal giocatore attivo in quel turno
            if (!this.partita.getGiocatoreAttivo().equals(toGiocatore(rinforzoDTO.getGiocatore())))
                throw new MossaIllegaleException();

            // blocca il rinforzo se non ha armate da piazzare ( cioè è già stato fatto )
            if (giocatore.getTruppeDisponibili() == 0)
                throw new MossaIllegaleException();

            eseguiRinforzo(rinforzoDTO, partita.getGiocatoreAttivo().getTruppeDisponibili());
        }
    }

    private void eseguiRinforzo(RinforzoDTO rinforzoDTO, int armateDaPiazzare) {
        int totale = 0;
        // controlla che i rinforzi totali siano quanto richiesto
        for (Long key : rinforzoDTO.getRinforzi().keySet()) {
            totale = totale + rinforzoDTO.getRinforzi().get(key);

            // controlla che ogni stato sia del giocatore attivo
            if (!toStato(key).getProprietario().equals(partita.getGiocatoreAttivo()))
                throw new MossaIllegaleException();
        }
        if (totale != armateDaPiazzare)
            throw new MossaIllegaleException();
        // crea ed esegue un nuovo rinforzo per ogni coppia idStato-numeroArmate della mappa in ingresso
        for (Long key : rinforzoDTO.getRinforzi().keySet()) {
            Rinforzo rinforzo = new Rinforzo(toStato(key), rinforzoDTO.getRinforzi().get(key));
            rinforzo.esegui();
        }

        partita.getGiocatoreAttivo().setTruppeDisponibili(0);
    }

    // TODO:: carte territorio
    public int giocaTris(TrisDTO trisDTO) {
        // blocca il metodo se si è in fase di preparazione
        if (fasePreparazione)
            throw new MossaIllegaleException();

        // blocca gioca tris se non viene chiamata dal giocatore attivo in quel turno
        if (!this.partita.getTurno().getGiocatoreAttivo().equals(toGiocatore(trisDTO.getGiocatore())))
            throw new MossaIllegaleException();

        // blocca gioca tris se il turno non è' in fase di rinforzo
        if (!this.partita.getTurno().getFase().equals(Turno.Fase.RINFORZI))
            throw new MossaIllegaleException();

        // TODO: bisogna passare dall'id degli stati alle carte territorio ( e i jolly ?) e fare i dovuti controlli
        // int nArmateBonus = carteTerritorioService.valutaTris();
        // carteTerritorio.rimettiNelMazzo (.... )
        int nArmateBonus = 1;
        this.partita.getTurno().getGiocatoreAttivo().addTruppeDisponibili(1);

        // TODO: rivedere il tipo ritornato
        return nArmateBonus;
    }

    public void attacco(AttaccoDTO attaccoDTO) {
        // blocca il metodo se si è in fase di preparazione
        if (fasePreparazione)
            throw new MossaIllegaleException();

        // blocca l'attacco se in fase di spostamento
        if (this.partita.getTurno().getFase().equals(Turno.Fase.SPOSTAMENTO))
            throw new MossaIllegaleException();

        // blocca l'attacco se il giocatore attivo ha ancora truppe da posizionare
        if (this.partita.getTurno().getGiocatoreAttivo().getTruppeDisponibili() != 0)
            throw new MossaIllegaleException();

        // blocca l'attacco se c'e' un combattimento in corso
        if (this.partita.getTurno().getCombattimentoInCorso() != null)
            throw new MossaIllegaleException();

        // blocca l'attacco se cerca di attaccare un proprio territorio
        if (this.partita.getTurno().getGiocatoreAttivo().equals(toStato(attaccoDTO.getDifensore()).getProprietario()))
            throw new MossaIllegaleException();

        // blocca l'attacco se non viene chiamato dal giocatore attivo in quel turno
        if (this.partita.getTurno().getGiocatoreAttivo().equals(toStato(attaccoDTO.getAttaccante()).getProprietario()))
            throw new MossaIllegaleException();

        this.partita.getTurno().setFase(Turno.Fase.COMBATTIMENTI);
        this.partita.getTurno().setCombattimentoInCorso(
                new Combattimento(toStato(attaccoDTO.getAttaccante()),
                        toStato(attaccoDTO.getDifensore()),
                        attaccoDTO.getArmate()));
    }

    public DifesaDAO difesa(DifesaDTO difesaDTO) {
        // blocca il metodo se si è in fase di preparazione
        if (fasePreparazione)
            throw new MossaIllegaleException();

        // blocca la difesa se non c'e' un combattimento in corso
        if (this.partita.getTurno().getCombattimentoInCorso() == null)
            throw new MossaIllegaleException();

        // blocca la difesa se non si è in fase di combattimento
        if (!this.partita.getTurno().getFase().equals(Turno.Fase.COMBATTIMENTI))
            throw new MossaIllegaleException();

        // blocca la difesa se chiamata da un giocatore non coinvolto come difensore nell'attuale combattimento
        if (!this.partita.getTurno().getCombattimentoInCorso().getStatoDifensore().getProprietario().equals(
                toGiocatore(difesaDTO.getGiocatore())))
            throw new MossaIllegaleException();

        this.partita.getTurno().getCombattimentoInCorso().simulaCombattimento(difesaDTO.getArmate());
        DifesaDAO difesaDAO = new DifesaDAO(this.partita.getTurno().getCombattimentoInCorso());
        this.partita.getTurno().setCombattimentoInCorso(null);
        return difesaDAO;
    }

    public void spostamentoStrategico(SpostamentoDTO spostamentoDTO) {
        // blocca il metodo se si è in fase di preparazione
        if (fasePreparazione)
            throw new MossaIllegaleException();

        // blocca lo spostamento se non viene chiamato dal giocatore attivo in quel turno
        if (!this.partita.getTurno().getGiocatoreAttivo().equals(toGiocatore(spostamentoDTO.getGiocatore())))
            throw new MossaIllegaleException();

        if (this.partita.getTurno().getCombattimentoInCorso() != null) {
            SpostamentoStrategico spostamentoAttacco = new SpostamentoStrategico(
                    toStato(spostamentoDTO.getStatoPartenza()),
                    toStato(spostamentoDTO.getStatoArrivo()),
                    Integer.max(this.partita.getTurno().getCombattimentoInCorso().getArmateAttaccante() -
                                    this.partita.getTurno().getCombattimentoInCorso().getVittimeAttaccante(),
                            spostamentoDTO.getArmate()));
            spostamentoAttacco.esegui();
            this.partita.getTurno().setCombattimentoInCorso(null);
        } else {
            this.partita.getTurno().setFase(Turno.Fase.SPOSTAMENTO);
            SpostamentoStrategico spostamentoStrategico = new SpostamentoStrategico(
                    toStato(spostamentoDTO.getStatoPartenza()),
                    toStato(spostamentoDTO.getStatoArrivo()),
                    spostamentoDTO.getArmate());
            spostamentoStrategico.esegui();
        }
    }

    public CartaTerritorioDAO fineTurno() {
        // blocca il metodo se si è in fase di preparazione
        if (fasePreparazione)
            throw new MossaIllegaleException();

        CartaTerritorio cartaTerritorio = null;
        if (partita.getTurno().conquistaAvvenuta()) {
            cartaTerritorio = carteTerritorioService.pescaCarta(partita.getGiocatoreAttivo());
        }

        partita.nuovoTurno();

        return cartaTerritorio != null ? new CartaTerritorioDAO(cartaTerritorio): null;
    }


    public Stato toStato(Long idStato) {
        List<Stato> stato;
        stato = this.partita.getMappa().getStati().stream().filter(
                s -> s.getId().equals(idStato)
        ).collect(Collectors.toList());
        if (stato.get(0) == null)
            throw new RuntimeException("Stato non esiste");
        return stato.get(0);
    }

    public Giocatore toGiocatore(String nomeGiocatore) {
        Giocatore giocatore = null;
        for (int i = 0; i < this.partita.getGiocatori().size(); i++) {
            if (nomeGiocatore.equalsIgnoreCase(this.partita.getGiocatori().get(i).getNome()))
                giocatore = this.partita.getGiocatori().get(i);
        }
        if (giocatore == null)
            throw new RuntimeException("Giocatore non esiste");
        return giocatore;
    }
}