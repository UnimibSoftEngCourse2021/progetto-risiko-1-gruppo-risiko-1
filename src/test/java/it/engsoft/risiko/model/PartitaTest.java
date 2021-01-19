package it.engsoft.risiko.model;

import it.engsoft.risiko.exceptions.ModelDataException;
import it.engsoft.risiko.repository.MappaRepository;
import it.engsoft.risiko.service.CarteTerritorioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PartitaTest {
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
        assertEquals(partita.getGiocatoreAttivo(), partita.getGiocatori().get(0));

        // se esegue di nuovo il metodo da errore
        try {
            partita.occupazioneInizialeTerritori();
            fail();
        } catch (ModelDataException ignored) { }

        assertTrue(partita.isFasePreparazione());

        // prova a impostare il turno mentre in fase di preparazione
        try {
            partita.iniziaPrimoTurno();
            fail();
        } catch (ModelDataException ignored) { }

        // prova a iniziare il nuovo turno mentre in fase di preparazione
        try {
            partita.nuovoTurno();
            fail();
        } catch (ModelDataException ignored) { }
    }
}
