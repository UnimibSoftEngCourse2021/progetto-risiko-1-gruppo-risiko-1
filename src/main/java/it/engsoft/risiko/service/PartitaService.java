package it.engsoft.risiko.service;

import it.engsoft.risiko.exceptions.MossaIllegaleException;
import it.engsoft.risiko.model.*;
import it.engsoft.risiko.repository.MappaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;

@Service
@Scope(value = WebApplicationContext.SCOPE_SESSION)
public class PartitaService {
    private Partita partita;
    @Autowired
    private MappaRepository mappaRepository;

    public void nuovoGioco(ArrayList<Giocatore> giocatori, Long id, Partita.Modalita modalita) {
        this.partita = new Partita();
        this.partita.giocatori = giocatori;
        this.partita.mappa = mappaRepository.findById(id);
        this.partita.modalita = modalita;
    }

    public void iniziaTurno(Giocatore giocatore, int numero) {
        this.partita.giocatoreAttivo = giocatore;
        this.partita.turno = new Turno(giocatore, numero);
    }

    public void rinforzo(Giocatore giocatore, Stato target) {
        if (!(this.partita.turno.getGiocatoreAttivo() == giocatore))
            throw new MossaIllegaleException();
        this.partita.turno.setFase(Turno.Fase.RINFORZI);
        this.partita.turno.setRinforzo(new Rinforzo(target, giocatore.getTruppeDisponibili()));
        this.partita.turno.getRinforzo().esegui();
    }

    // sistemare posizione metodi relativi al ritorno di truppe date da un tris
    public void giocaTris(Giocatore giocatore, CartaTerritorio x, CartaTerritorio y, CartaTerritorio z) {
        this.partita.turno.getGiocatoreAttivo().addTruppeDisponibili(x.TruppeTris(x, y, z));
    }

    public void combattimento(Giocatore giocatore, Stato statoAttaccante, Stato statoDifensore,
                              int armateAtt, int armateDif, int spostamentoTruppe) {
        this.partita.turno.setFase(Turno.Fase.COMBATTIMENTI);
        this.partita.turno.setCombattimentoInCorso(new Combattimento(statoAttaccante, statoDifensore, armateAtt));
        this.partita.turno.getCombattimentoInCorso().simulaCombattimento(armateDif);
        this.partita.turno.getCombattimentoInCorso().setSpostamentoAttacco(new SpostamentoStrategico(
                statoAttaccante, statoDifensore, Integer.max(
                        armateAtt - partita.turno.getCombattimentoInCorso().getVittimeAttaccante(),
                                spostamentoTruppe)));
    }

    public void spostamentoStrategico(Stato partenza) {
    }


}
