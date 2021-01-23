package it.engsoft.risiko.data.model;

import it.engsoft.risiko.exceptions.ModelDataException;
import it.engsoft.risiko.data.repository.MappaRepository;
import it.engsoft.risiko.service.CarteTerritorioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PartitaTest {
    @Autowired
    MappaRepository mappaRepository;
    @Autowired
    CarteTerritorioService carteTerritorioService;

    @Test
    void testCreazione() {
        Modalita modalita = Modalita.COMPLETA;
        Mappa mappa = mappaRepository.findById(1L).orElse(null);
        List<Giocatore> giocatori = new ArrayList<>();
        for(int i = 0; i < 5; i++) {
            giocatori.add(new Giocatore("Giocatore" + i));
        }

        // Creazione corretta
        Partita partita = new Partita(mappa, giocatori, modalita);

        // mappa null
        try {
            partita = new Partita(null, giocatori, modalita);
            fail();
        } catch (ModelDataException ignored) { }

        // giocatori null
        try {
            partita = new Partita(mappa, null, modalita);
            fail();
        } catch (ModelDataException ignored) { }


        // modalita null
        try {
            partita = new Partita(mappa, giocatori, null);
            fail();
        } catch (ModelDataException ignored) { }
    }

    @Test
    void testPassaggiInizialiPartita() {
        Modalita modalita = Modalita.COMPLETA;
        Mappa mappa = mappaRepository.findById(1L).orElse(null);
        List<Giocatore> giocatori = new ArrayList<>();
        for(int i = 0; i < 5; i++) {
            giocatori.add(new Giocatore("Giocatore" + i));
        }

        Partita partita = new Partita(mappa, giocatori, modalita);

        // prova ad occupare i territori senza aver prima distribuito le carte territorio
        try {
            partita.occupazioneInizialeTerritori();
            fail();
        } catch (ModelDataException ignored) { }

        carteTerritorioService.distribuisciCarte(partita);

        // prova a chiedere il giocatore attivo prima di aver occupato i territori
        try {
            partita.getGiocatoreAttivo();
            fail();
        } catch (ModelDataException ignored) { }

        partita.occupazioneInizialeTerritori();
        assertEquals(partita.getGiocatori().get(0), partita.getGiocatoreAttivo());

        // se esegue di nuovo il metodo da errore
        try {
            partita.occupazioneInizialeTerritori();
            fail();
        } catch (ModelDataException ignored) { }

        assertTrue(partita.isFasePreparazione());

        // prova a iniziare il nuovo turno mentre in fase di preparazione
        try {
            partita.nuovoTurno();
            fail();
        } catch (ModelDataException ignored) { }

        // prova a terminare la fase di preparazione prima del dovuto
        try {
            partita.setFasePreparazione(false);
            fail();
        } catch (ModelDataException ignored) { }
    }

    @Test
    void testOccupazioneInizialeTerritori() {
        Modalita modalita = Modalita.COMPLETA;
        Mappa mappa = mappaRepository.findById(1L).orElse(null);
        List<Giocatore> giocatori = new ArrayList<>();
        for(int i = 0; i < 5; i++) {
            giocatori.add(new Giocatore("Giocatore" + i));
        }

        Partita partita = new Partita(mappa, giocatori, modalita);
        int armateIniziali = giocatori.get(0).getTruppeDisponibili();
        // controlla che tutti abbiano lo stesso n di truppe iniziali
        assertFalse(
                giocatori.stream().anyMatch(g -> g.getTruppeDisponibili() != armateIniziali)
        );

        carteTerritorioService.distribuisciCarte(partita);
        partita.occupazioneInizialeTerritori();

        // ogni stato ha esattamente un'armata
        assertFalse(
                partita.getMappa().getStati().stream().anyMatch(s -> s.getArmate() != 1)
        );

        // controlla che le truppe dei giocatori siano state correttamente aggiornate
        assertFalse(
                partita.getGiocatori().stream().anyMatch(g -> (g.getStati().size() + g.getTruppeDisponibili()) != armateIniziali)
        );
    }

    @Test
    void testRotazioneGiocatoriFaseIniziale() {
        Modalita modalita = Modalita.COMPLETA;
        Mappa mappa = mappaRepository.findById(1L).orElse(null);
        List<Giocatore> giocatori = new ArrayList<>();
        for(int i = 0; i < 3; i++) {
            giocatori.add(new Giocatore("Giocatore" + i));
        }

        Partita partita = new Partita(mappa, giocatori, modalita);
        carteTerritorioService.distribuisciCarte(partita);
        partita.occupazioneInizialeTerritori();

        Giocatore giocatoreAttivo = partita.getGiocatoreAttivo();
        assertEquals(giocatori.get(0), giocatoreAttivo);
        partita.setNuovoGiocatoreAttivoPreparazione();

        giocatoreAttivo = partita.getGiocatoreAttivo();
        assertEquals(giocatori.get(1), giocatoreAttivo);
        partita.setNuovoGiocatoreAttivoPreparazione();

        giocatoreAttivo = partita.getGiocatoreAttivo();
        assertEquals(giocatori.get(2), giocatoreAttivo);
        partita.setNuovoGiocatoreAttivoPreparazione();

        giocatoreAttivo = partita.getGiocatoreAttivo();
        assertEquals(giocatori.get(0), giocatoreAttivo);
    }

    @Test
    void testRotazioneTurni() {
        Modalita modalita = Modalita.COMPLETA;
        Mappa mappa = mappaRepository.findById(1L).orElse(null);
        List<Giocatore> giocatori = new ArrayList<>();
        for(int i = 0; i < 3; i++) {
            giocatori.add(new Giocatore("Giocatore" + i));
        }
        Partita partita = new Partita(mappa, giocatori, modalita);
        carteTerritorioService.distribuisciCarte(partita);
        partita.occupazioneInizialeTerritori();

        // prima assegniamo arbitrariamente i rinforzi
        for (Giocatore g: giocatori) {
            g.getStati().get(0).aggiungiArmate(g.getTruppeDisponibili());
            g.setTruppeDisponibili(0);
        }

        partita.setNuovoGiocatoreAttivoPreparazione();
        assertFalse(partita.isFasePreparazione());

        Giocatore giocAttivo = partita.getGiocatoreAttivo();
        assertEquals(giocatori.get(0), giocAttivo);

        // TODO: considerare l'assegnazione delle truppe alla creazione del turno

        partita.nuovoTurno();
        giocAttivo = partita.getGiocatoreAttivo();
        assertEquals(giocatori.get(1), giocAttivo);

        partita.nuovoTurno();
        giocAttivo = partita.getGiocatoreAttivo();
        assertEquals(giocatori.get(2), giocAttivo);

        partita.nuovoTurno();
        giocAttivo = partita.getGiocatoreAttivo();
        assertEquals(giocatori.get(0), giocAttivo);

        // eliminiamo il giocatore 1
        Giocatore eliminato = giocatori.get(1);
        while (eliminato.getStati().size() > 0) {
            eliminato.getStati().remove(0);
        }
        partita.nuovoTurno();
        giocAttivo = partita.getGiocatoreAttivo();
        assertEquals(giocatori.get(2), giocAttivo);
    }
}
