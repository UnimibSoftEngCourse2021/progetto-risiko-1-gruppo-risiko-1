package it.engsoft.risiko.data.model;

import it.engsoft.risiko.exceptions.ModelDataException;
import it.engsoft.risiko.data.repository.MappaRepository;
import it.engsoft.risiko.exceptions.MossaIllegaleException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CombattimentoTest {
    @Autowired
    MappaRepository mappaRepository;

    @Test
    void testCostruzione() {
        Giocatore giocAttaccante = new Giocatore("Pippo");
        Giocatore giocDifensore = new Giocatore("Pluto");

        Mappa mappa = mappaRepository.findById(1L).orElse(null);
        Stato attaccante = mappa.getStati().get(0);
        attaccante.aggiungiArmate(4);
        attaccante.setProprietario(giocAttaccante);
        Stato difensore = attaccante.getConfinanti().get(0);
        difensore.aggiungiArmate(4);
        difensore.setProprietario(giocDifensore);

        // costruzione corretta
        Combattimento combattimento = new Combattimento(attaccante, difensore, 3);
        assertEquals(attaccante, combattimento.getStatoAttaccante());
        assertEquals(difensore, combattimento.getStatoDifensore());
        assertEquals(3, combattimento.getArmateAttaccante());

        // attaccante null
        try {
            combattimento = new Combattimento(null, difensore, 3);
            fail();
        } catch (ModelDataException ignored) { }

        // difensore null
        try {
            combattimento = new Combattimento(attaccante, null, 3);
            fail();
        } catch (ModelDataException ignored) { }

        // stesso giocatore
        try {
            combattimento = new Combattimento(attaccante, attaccante, 3);
            fail();
        } catch (MossaIllegaleException ignored) { }

        // non confinante
        Stato nonConf = mappa.getStati().stream().filter(s -> !attaccante.getConfinanti().contains(s)).findFirst().get();
        nonConf.aggiungiArmate(4);
        nonConf.setProprietario(giocDifensore);
        try {
            combattimento = new Combattimento(attaccante, nonConf, 3);
            fail();
        } catch (MossaIllegaleException ignored) { }

        // troppe poche armate
        try {
            combattimento = new Combattimento(attaccante, difensore, 0);
            fail();
        } catch (MossaIllegaleException ignored) { }

        // più di 3 armate
        try {
            combattimento = new Combattimento(attaccante, difensore, 4);
            fail();
        } catch (MossaIllegaleException ignored) { }


        // più armate di quelle a disposizione
        attaccante.rimuoviArmate(1);
        try {
            combattimento = new Combattimento(attaccante, difensore, 3);
            fail();
        } catch (MossaIllegaleException ignored) { }
    }

    @Test
    void testCheckParametriSimulazione() {
        Giocatore giocAttaccante = new Giocatore("Pippo");
        Giocatore giocDifensore = new Giocatore("Pluto");

        Mappa mappa = mappaRepository.findById(1L).orElse(null);
        Stato attaccante = mappa.getStati().get(0);
        attaccante.aggiungiArmate(4);
        attaccante.setProprietario(giocAttaccante);
        Stato difensore = attaccante.getConfinanti().get(0);
        difensore.aggiungiArmate(4);
        difensore.setProprietario(giocDifensore);

        // costruzione corretta
        Combattimento combattimento = new Combattimento(attaccante, difensore, 3);

        // più di 3 armate dif
        try {
            combattimento.esegui(4);
            fail();
        } catch (MossaIllegaleException ignored) { }

        // armate negative dif
        try {
            combattimento.esegui(-1);
            fail();
        } catch (MossaIllegaleException ignored) { }

        // troppe armate dif
        difensore.rimuoviArmate(2);
        try {
            combattimento.esegui(3);
            fail();
        } catch (MossaIllegaleException ignored) { }
    }

    @Test
    void testSimulazione() {
        Giocatore giocAttaccante = new Giocatore("Pippo");
        Giocatore giocDifensore = new Giocatore("Pluto");

        Mappa mappa = mappaRepository.findById(1L).orElse(null);
        Stato attaccante = mappa.getStati().get(0);
        attaccante.aggiungiArmate(4);
        attaccante.setProprietario(giocAttaccante);
        Stato difensore = attaccante.getConfinanti().get(0);
        difensore.aggiungiArmate(2);
        difensore.setProprietario(giocDifensore);
        giocAttaccante.aggiungiStato(attaccante);
        giocDifensore.aggiungiStato(difensore);

        Combattimento combattimento = new Combattimento(attaccante, difensore, 3);

        combattimento.esegui(2);

        assertEquals(3, combattimento.getTiriAttaccante().size());
        assertEquals(2, combattimento.getTiriDifensore().size());

        combattimento.getTiriAttaccante().forEach(tiro -> {
            assertTrue(tiro >= 1 && tiro <= 6);
        });

        combattimento.getTiriDifensore().forEach(tiro -> {
            assertTrue(tiro >= 1 && tiro <= 6);
        });

        assertEquals(2, combattimento.getVittimeAttaccante() + combattimento.getVittimeDifensore());

        assertEquals(combattimento.getVittimeDifensore() == 2, combattimento.getConquista());
    }
}
