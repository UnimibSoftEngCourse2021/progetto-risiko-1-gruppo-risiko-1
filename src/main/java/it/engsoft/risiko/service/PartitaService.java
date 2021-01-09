package it.engsoft.risiko.service;

import it.engsoft.risiko.dao.*;
import it.engsoft.risiko.dto.*;
import it.engsoft.risiko.exceptions.*;
import it.engsoft.risiko.model.*;
import it.engsoft.risiko.repository.MappaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;
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
        partita.setMappa(mappaRepository.findById(nuovoGiocoDTO.getMappaId()));

        // controllo sul valore minimo e massimo di giocatori ammessi dalla mappa
        if (partita.getMappa().getNumMaxGiocatori() < nuovoGiocoDTO.getGiocatori().size() ||
                partita.getMappa().getNumMinGiocatori() > nuovoGiocoDTO.getGiocatori().size())
            throw new RuntimeException("Ecceduto il limite massimo-minimo di giocatori");

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
        partita.getGiocatori().forEach(giocatore -> {
            giocatore.getStati().forEach(stato -> {
                stato.aggiungiArmate(1);
                giocatore.modificaTruppeDisponibili(-1);
            });
        });

        // setta la modalità
        partita.setModalita(Partita.Modalita.valutaModalita(nuovoGiocoDTO.getMod()));


        partita.setFasePreparazione(true);

        // scegliamo casualmente un ordine di giocatori
        Collections.shuffle(partita.getGiocatori());

        // viene impostato manualmente solo la prima volta, il primo della lista randomizzata
        partita.setGiocatoreAttivo(partita.getGiocatori().get(0));

        return partita;
    }

    public IniziaTurnoDAO iniziaTurno(Partita partita) {
        if(partita == null)
            throw new MossaIllegaleException();

        int armateContinenti = 0;
        int armateStati = 0;

        for (Continente continente : partita.getMappa().getContinenti()) {
            if(partita.getGiocatoreAttivo().equals(continente.getProprietario()))
                armateContinenti = continente.getArmateBonus();
        }

        armateStati = partita.getTurno().getGiocatoreAttivo().getStati().size() / 3;
        return new IniziaTurnoDAO(partita.getTurno(), armateStati, armateContinenti, armateStati + armateContinenti);
    }

    public boolean rinforzo(RinforzoDTO rinforzoDTO, Partita partita) {
        if(partita == null)
            throw new MossaIllegaleException();

        // gestione rinforzi iniziali, credo convenga aggiungere dei controlli sul numero di armate in input da lato client
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

            // blocca il rinforzo se non viene chiamato dal giocatore attivo in quel turno
            if (!partita.getGiocatoreAttivo().equals(toGiocatore(rinforzoDTO.getGiocatore(), partita)))
                throw new MossaIllegaleException();

            // blocca il rinforzo se non ha armate da piazzare ( cioè è già stato fatto )
            if (giocatore.getTruppeDisponibili() == 0)
                throw new MossaIllegaleException();

            eseguiRinforzo(rinforzoDTO, partita.getGiocatoreAttivo().getTruppeDisponibili(), partita);
        }

        return partita.isFasePreparazione();
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
        if(partita == null)
            throw new MossaIllegaleException();

        // blocca il metodo se si è in fase di preparazione
        if (partita.isFasePreparazione())
            throw new MossaIllegaleException();

        // blocca gioca tris se il turno non è' in fase di rinforzo
        if (!partita.getTurno().getFase().equals(Turno.Fase.RINFORZI))
            throw new MossaIllegaleException();

        // blocca gioca tris se in trisDTO non sono contenute esattamente tre carte
        if (trisDTO.getTris().size() != 3)
            throw new MossaIllegaleException();

        // seleziona giocatore che gioca il tris
        Giocatore giocatore = toGiocatore(trisDTO.getGiocatore(), partita);

        // blocca gioca tris se non viene chiamata dal giocatore attivo in quel turno
        if (!partita.getTurno().getGiocatoreAttivo().equals(giocatore))
            throw new MossaIllegaleException();

        int nArmateBonus = carteTerritorioService.valutaTris(partita.getMazzo(), trisDTO.getTris(), giocatore);
        partita.getTurno().getGiocatoreAttivo().modificaTruppeDisponibili(nArmateBonus);

        return nArmateBonus;
    }

    public void attacco(AttaccoDTO attaccoDTO, Partita partita) {
        if(partita == null)
            throw new MossaIllegaleException();

        // blocca il metodo se si è in fase di preparazione
        if (partita.isFasePreparazione())
            throw new MossaIllegaleException();

        // blocca l'attacco se in fase di spostamento
        if (partita.getTurno().getFase().equals(Turno.Fase.SPOSTAMENTO))
            throw new MossaIllegaleException();

        // blocca l'attacco se il giocatore attivo ha ancora truppe da posizionare
        if (partita.getTurno().getGiocatoreAttivo().getTruppeDisponibili() != 0)
            throw new MossaIllegaleException();

        // blocca l'attacco se c'e' un combattimento in corso
        if (partita.getTurno().getCombattimentoInCorso() != null)
            throw new MossaIllegaleException();

        // blocca l'attacco se cerca di attaccare un proprio territorio
        if (partita.getTurno().getGiocatoreAttivo().equals(toStato(attaccoDTO.getDifensore(), partita).getProprietario()))
            throw new MossaIllegaleException();

        // blocca l'attacco se non viene chiamato dal giocatore attivo in quel turno
        if (partita.getTurno().getGiocatoreAttivo().equals(toStato(attaccoDTO.getAttaccante(), partita).getProprietario()))
            throw new MossaIllegaleException();

        partita.getTurno().setFase(Turno.Fase.COMBATTIMENTI);
        partita.getTurno().setCombattimentoInCorso(
                new Combattimento(toStato(attaccoDTO.getAttaccante(), partita),
                        toStato(attaccoDTO.getDifensore(), partita),
                        attaccoDTO.getArmate()));
    }

    public DifesaDAO difesa(DifesaDTO difesaDTO, Partita partita) {
        if(partita == null)
            throw new MossaIllegaleException();

        // blocca il metodo se si è in fase di preparazione
        if (partita.isFasePreparazione())
            throw new MossaIllegaleException();

        // blocca la difesa se non c'e' un combattimento in corso
        if (partita.getTurno().getCombattimentoInCorso() == null)
            throw new MossaIllegaleException();

        // blocca la difesa se non si è in fase di combattimento
        if (!partita.getTurno().getFase().equals(Turno.Fase.COMBATTIMENTI))
            throw new MossaIllegaleException();

        // blocca la difesa se chiamata da un giocatore non coinvolto come difensore nell'attuale combattimento
        if (!partita.getTurno().getCombattimentoInCorso().getStatoDifensore().getProprietario().equals(
                toGiocatore(difesaDTO.getGiocatore(), partita)))
            throw new MossaIllegaleException();

        partita.getTurno().getCombattimentoInCorso().simulaCombattimento(difesaDTO.getArmate());
        DifesaDAO difesaDAO = new DifesaDAO(partita.getTurno().getCombattimentoInCorso());
        partita.getTurno().setCombattimentoInCorso(null);
        return difesaDAO;
    }

    public void spostamentoStrategico(SpostamentoDTO spostamentoDTO, Partita partita) {
        if(partita == null)
            throw new MossaIllegaleException();

        // blocca il metodo se si è in fase di preparazione
        if (partita.isFasePreparazione())
            throw new MossaIllegaleException();

        // blocca lo spostamento se non viene chiamato dal giocatore attivo in quel turno
        if (!partita.getTurno().getGiocatoreAttivo().equals(toGiocatore(spostamentoDTO.getGiocatore(), partita)))
            throw new MossaIllegaleException();

        if (partita.getTurno().getCombattimentoInCorso() != null) {
            SpostamentoStrategico spostamentoAttacco = new SpostamentoStrategico(
                    toStato(spostamentoDTO.getStatoPartenza(), partita),
                    toStato(spostamentoDTO.getStatoArrivo(), partita),
                    Integer.max(partita.getTurno().getCombattimentoInCorso().getArmateAttaccante() -
                                    partita.getTurno().getCombattimentoInCorso().getVittimeAttaccante(),
                            spostamentoDTO.getArmate()));
            spostamentoAttacco.esegui();
            partita.getTurno().setCombattimentoInCorso(null);
        } else {
            partita.getTurno().setFase(Turno.Fase.SPOSTAMENTO);
            SpostamentoStrategico spostamentoStrategico = new SpostamentoStrategico(
                    toStato(spostamentoDTO.getStatoPartenza(), partita),
                    toStato(spostamentoDTO.getStatoArrivo(), partita),
                    spostamentoDTO.getArmate());
            spostamentoStrategico.esegui();
        }
    }

    public CartaTerritorioDAO fineTurno(Partita partita) {
        if(partita == null)
            throw new MossaIllegaleException();

        // blocca il metodo se si è in fase di preparazione
        if (partita.isFasePreparazione())
            throw new MossaIllegaleException();

        // pesca una carta territorio se conquistato
        CartaTerritorio cartaTerritorio = null;
        if (partita.getTurno().conquistaAvvenuta()) {
            cartaTerritorio = carteTerritorioService.pescaCarta(partita.getMazzo(), partita.getGiocatoreAttivo());
        }

        partita.nuovoTurno();

        return cartaTerritorio != null ? new CartaTerritorioDAO(cartaTerritorio) : null;
    }


    public Stato toStato(Long idStato, Partita partita) {
        List<Stato> stato;
        stato = partita.getMappa().getStati().stream().filter(
                s -> s.getId().equals(idStato)
        ).collect(Collectors.toList());
        if (stato.get(0) == null)
            throw new RuntimeException("Stato non esiste");
        return stato.get(0);
    }

    public Giocatore toGiocatore(String nomeGiocatore, Partita partita) {
        Giocatore giocatore = null;
        for (int i = 0; i < partita.getGiocatori().size(); i++) {
            if (nomeGiocatore.equalsIgnoreCase(partita.getGiocatori().get(i).getNome()))
                giocatore = partita.getGiocatori().get(i);
        }
        if (giocatore == null)
            throw new DatiErratiException();
        return giocatore;
    }
}