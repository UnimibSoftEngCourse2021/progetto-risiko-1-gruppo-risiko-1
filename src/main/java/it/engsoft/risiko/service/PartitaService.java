package it.engsoft.risiko.service;

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

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Scope(value = WebApplicationContext.SCOPE_SESSION)
public class PartitaService {
    int statiIniziali;
    private Partita partita;
    private Boolean fasePreparazione;
    @Autowired
    private MappaRepository mappaRepository;

    // @Autowired in questa posizione da problemi con i parametri in ingresso
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

        // setta la modalità
        if (nuovoGiocoDTO.getMod().equalsIgnoreCase(Partita.Modalita.LENTA.toString()))
            this.partita.setModalita(Partita.Modalita.LENTA);
        if (nuovoGiocoDTO.getMod().equalsIgnoreCase(Partita.Modalita.NORMALE.toString()))
            this.partita.setModalita(Partita.Modalita.NORMALE);
        if (nuovoGiocoDTO.getMod().equalsIgnoreCase(Partita.Modalita.VELOCE.toString()))
            this.partita.setModalita(Partita.Modalita.VELOCE);


        fasePreparazione = true;

        return new NuovoGiocoDAO(this.partita);
    }

    public IniziaTurnoDAO iniziaTurno() {
        return new IniziaTurnoDAO(this.partita.getTurno());
    }

    public void rinforzo(RinforzoDTO rinforzoDTO) {
        // gestione rinforzi iniziali, credo convenga aggiungere dei controlli sul numero di armate in input da lato client
        if (fasePreparazione) {
            int counter = 0;
            while (this.partita.getGiocatori().get(this.partita.getGiocatori().size()).getTruppeDisponibili() > 0) {
                for (int i = 0; i < this.partita.getGiocatori().size(); i++) {
                    for (Long key : rinforzoDTO.getRinforzi().keySet()) {
                        counter = counter + rinforzoDTO.getRinforzi().get(key);
                        Rinforzo rinforzo = new Rinforzo(toStato(key), rinforzoDTO.getRinforzi().get(key));
                        rinforzo.esegui();
                    }
                    if (counter > 3)
                        throw new MossaIllegaleException();
                }
            }
            fasePreparazione = false;
        } else {
            // blocca il rinforzo se non si è in fase di rinforzi
            if (!this.partita.getTurno().getFase().equals(Turno.Fase.RINFORZI))
                throw new MossaIllegaleException();

            // blocca il rinforzo se non viene chiamato dal giocatore attivo in quel turno
            if (!this.partita.getGiocatoreAttivo().equals(toGiocatore(rinforzoDTO.getGiocatore())))
                throw new MossaIllegaleException();

            // crea ed esegue un nuovo rinforzo per ogni coppia idStato-numeroArmate della mappa in ingresso
            for (Long key : rinforzoDTO.getRinforzi().keySet()) {
                Rinforzo rinforzo = new Rinforzo(toStato(key), rinforzoDTO.getRinforzi().get(key));
                rinforzo.esegui();
            }
        }
    }

    // TODO:: carte territorio
    public void giocaTris(TrisDTO trisDTO) {
        // blocca il metodo se si è in fase di preparazione
        if (fasePreparazione)
            throw new MossaIllegaleException();

        // blocca gioca tris se non viene chiamata dal giocatore attivo in quel turno
        if (!this.partita.getTurno().getGiocatoreAttivo().equals(toGiocatore(trisDTO.getGiocatore())))
            throw new MossaIllegaleException();

        // blocca gioca tris se il turno non è' in fase di rinforzo
        if (!this.partita.getTurno().getFase().equals(Turno.Fase.RINFORZI))
            throw new MossaIllegaleException();

        this.partita.getTurno().getGiocatoreAttivo().addTruppeDisponibili(1);
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

        // blocca la difesa se chiamata dal giocatore attivo in quel turno
        if (this.partita.getTurno().getGiocatoreAttivo().equals(toGiocatore(difesaDTO.getGiocatore())))
            throw new MossaIllegaleException();

        // blocca la difesa se chiamata da n giocatore non coinvolto nell'attuale combattimento
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

    public void fineTurno() {
        // blocca il metodo se si è in fase di preparazione
        if (fasePreparazione)
            throw new MossaIllegaleException();

        // TODO:: ritorna al giocatore una carta territorio (se ha conquistato almeno uno stato)

        // inizializza primo turno
        if (this.partita.getTurno() == null) {
            // set giocatoreAttivo passandogli la lista dei giocatori e selezionandone uno randomicamente
            Random r = new Random();
            this.partita.setGiocatoreAttivo(partita.getGiocatori().get(r.nextInt(partita.getGiocatori().size())));

            // creazione del primo turno
            this.partita.setTurno(new Turno(this.partita.getGiocatoreAttivo(), 1));
        } else {
            // setta come giocatore attivo il giocatore successivo della lista giocatori
            for (int i = 0; i < this.partita.getGiocatori().size(); i++) {
                if (this.partita.getGiocatoreAttivo().equals(this.partita.getGiocatori().get(i))) {
                    if (i == this.partita.getGiocatori().size() - 1)
                        this.partita.setGiocatoreAttivo(this.partita.getGiocatori().get(0));
                    this.partita.setGiocatoreAttivo(this.partita.getGiocatori().get(i + 1));
                }
            }

            // istanzia un nuovo turno passandogli il nuovo giocatoreAttivo e il numero del turno precedente incrementato di 1
            this.partita.setTurno(
                    new Turno(this.partita.getGiocatoreAttivo(),
                            this.partita.getTurno().getNumero() + 1)
            );
        }
    }


    // TODO:: alla fine rimuovere i metodi non utilizzati
    public Stato toStato(String nomeStato) {
        Stato stato = null;
        for (int i = 0; i < this.partita.getTurno().getGiocatoreAttivo().getStati().size(); i++) {
            if (nomeStato.equalsIgnoreCase(this.partita.getTurno().getGiocatoreAttivo().getStati().get(i).getNome()))
                stato = this.partita.getTurno().getGiocatoreAttivo().getStati().get(i);
        }
        if (stato == null)
            throw new RuntimeException("Stato non esiste");

        return stato;
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