package it.engsoft.risiko.service;

import it.engsoft.risiko.dao.NuovoGiocoDAO;
import it.engsoft.risiko.exceptions.MossaIllegaleException;
import it.engsoft.risiko.model.*;
import it.engsoft.risiko.repository.MappaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;

@Service
@Scope(value = WebApplicationContext.SCOPE_SESSION)
public class PartitaService {
    Scanner scanner = new Scanner(System.in);
    int statiIniziali;
    private Partita partita;
    @Autowired
    private MappaRepository mappaRepository;


    // @Autowired in questa posizione da problemi con i parametri in ingresso
    public NuovoGiocoDAO nuovoGioco(List<String> giocatori, Long mappaId, String modalita) {
        this.partita = new Partita();
        this.partita.setGiocatori(giocatori
                .stream()
                .map(Giocatore::new)
                .collect(Collectors.toList()));
        this.partita.setMappa(mappaRepository.findById(mappaId));

        if (modalita.equalsIgnoreCase(Partita.Modalita.LENTA.toString()))
            this.partita.setModalita(Partita.Modalita.LENTA);
        if (modalita.equalsIgnoreCase(Partita.Modalita.NORMALE.toString()))
            this.partita.setModalita(Partita.Modalita.NORMALE);
        if (modalita.equalsIgnoreCase(Partita.Modalita.VELOCE.toString()))
            this.partita.setModalita(Partita.Modalita.VELOCE);

        Random r = new Random();
        List<Giocatore> giocatori1 = giocatori.stream()
                .map(Giocatore::new)
                .collect(Collectors.toList());
        this.partita.setGiocatoreAttivo(giocatori1.get(r.nextInt(giocatori.size())));
        this.partita.setTurno(new Turno(this.partita.getGiocatoreAttivo(), 0));

        return new NuovoGiocoDAO(this.partita);
    }

    public void iniziaTurno(String giocatore, int numero) {
        if (giocatore.equals(this.partita.getGiocatoreAttivo().getNome()))
            throw new MossaIllegaleException();
        this.partita.setGiocatoreAttivo(toGiocatore(giocatore));
        if (numero != this.partita.getTurno().getNumero() + 1)
            throw new RuntimeException("Numero turno inserito non valido");
        this.partita.setTurno(new Turno(this.partita.getGiocatoreAttivo(), numero));
    }

    public Turno getTurno() {
        return this.partita.getTurno();
    }

    public void rinforzo(String giocatore, String target) {
        if (!giocatore.equals(this.partita.turno.getGiocatoreAttivo().getNome()))
            throw new MossaIllegaleException();
        this.partita.getTurno().setFase(Turno.Fase.RINFORZI);
        this.partita.getTurno().setRinforzo(new Rinforzo(toStato(target), this.partita.getTurno().getGiocatoreAttivo().getTruppeDisponibili()));
        this.partita.getTurno().getRinforzo().esegui();
    }

    // sistemare posizione metodi relativi al ritorno di truppe date da un tris
    public int giocaTris(String giocatore, CartaTerritorio x, CartaTerritorio y, CartaTerritorio z) {
        int armate = 0;
        armate = x.TruppeTris(x, y, z);
        this.partita.getTurno().getGiocatoreAttivo().addTruppeDisponibili(armate);
        return armate;
    }

    public void attacco(String giocatore, String statoAttaccante, String statoDifensore, int armateAtt) {
        Stato statoAtt = toStato(statoAttaccante);
        Stato statoDif = toStato(statoDifensore);
        if(!toGiocatore(giocatore).equals(statoAtt.getProprietario()))
            throw new MossaIllegaleException();

        this.partita.turno.setFase(Turno.Fase.COMBATTIMENTI);
        this.partita.turno.setCombattimentoInCorso(
                new Combattimento(statoAtt, statoDif, armateAtt));
        difesa(statoDif.getProprietario(), statoAtt, armateAtt);

        int spostamentoTruppe = scanner.nextInt();
        if (statoDif.getProprietario().equals(statoAtt.getProprietario()))
            this.partita.turno.getCombattimentoInCorso().setSpostamentoAttacco(
                    new SpostamentoStrategico(statoAtt, statoDif,
                            Integer.max(
                                    Integer.max(
                                            armateAtt,
                                            partita.turno.getCombattimentoInCorso().getVittimeAttaccante()),
                                    spostamentoTruppe)));
    }

    // può essere chiamato solo da Attacco
    public void difesa(Giocatore giocatore, Stato statoConteso, int armateAtt) {
        int armateDif = scanner.nextInt();
        this.partita.getTurno().getCombattimentoInCorso().simulaCombattimento(armateDif);
        if(this.partita.getTurno().getCombattimentoInCorso().getConquista())
            //ritorna vittime e se conquistato
            ;

    }

    public void spostamentoStrategico(String giocatore, String statoPartenza, String statoArrivo, int armate) {
        this.partita.getTurno().setFase(Turno.Fase.SPOSTAMENTO);
        this.partita.getTurno().setSpostamentoStrategico(
                new SpostamentoStrategico(toStato(statoPartenza), toStato(statoArrivo), armate));
    }

    //@Autowired
    //public void fineTurno(){
    //    if(this.partita.getTurno().getGiocatoreAttivo().getStati().size() > statiIniziali)
    //        this.partita.getTurno().getGiocatoreAttivo().aggiungiCartaTerritorio(CartaTerritorioRepository);
    //}


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