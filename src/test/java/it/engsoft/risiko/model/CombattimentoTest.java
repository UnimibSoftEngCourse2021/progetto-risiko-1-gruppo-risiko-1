package it.engsoft.risiko.model;

import it.engsoft.risiko.exceptions.ModelDataException;
import it.engsoft.risiko.repository.MappaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CombattimentoTest {
    @Autowired
    MappaRepository mappaRepository;

    @Test
    public void testCostruzione() {
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
        assertEquals(combattimento.getStatoAttaccante(), attaccante);
        assertEquals(combattimento.getStatoDifensore(), difensore);
        assertEquals(combattimento.getArmateAttaccante(), 3);

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
        } catch (ModelDataException ignored) { }

        // non confinante
        try {
            Stato nonConf = mappa.getStati().stream().filter(s -> !attaccante.getConfinanti().contains(s)).findFirst().get();
            nonConf.aggiungiArmate(4);
            nonConf.setProprietario(giocDifensore);
            combattimento = new Combattimento(attaccante, nonConf, 3);
            fail();
        } catch (ModelDataException ignored) { }

        // troppe poche armate
        try {
            combattimento = new Combattimento(attaccante, difensore, 0);
            fail();
        } catch (ModelDataException ignored) { }

        // più di 3 armate
        try {
            combattimento = new Combattimento(attaccante, difensore, 4);
            fail();
        } catch (ModelDataException ignored) { }


        // più armate di quelle a disposizione
        attaccante.rimuoviArmate(1);
        try {
            combattimento = new Combattimento(attaccante, difensore, 3);
            fail();
        } catch (ModelDataException ignored) { }
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
            combattimento.simulaCombattimento(4);
            fail();
        } catch (ModelDataException ignored) { }

        // armate negative dif
        try {
            combattimento.simulaCombattimento(-1);
            fail();
        } catch (ModelDataException ignored) { }

        // troppe armate dif
        difensore.rimuoviArmate(2);
        try {
            combattimento.simulaCombattimento(3);
            fail();
        } catch (ModelDataException ignored) { }
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
        Combattimento combattimento = new Combattimento(attaccante, difensore, 3);

        combattimento.simulaCombattimento(2);

        assertEquals(combattimento.getTiriAttaccante().size(), 3);
        assertEquals(combattimento.getTiriDifensore().size(), 2);

        combattimento.getTiriAttaccante().forEach(tiro -> {
            assertTrue(tiro >= 1 && tiro <= 6);
        });

        combattimento.getTiriDifensore().forEach(tiro -> {
            assertTrue(tiro >= 1 && tiro <= 6);
        });

        assertEquals(combattimento.getVittimeAttaccante() + combattimento.getVittimeDifensore(), 2);

        assertEquals(combattimento.getConquista(), combattimento.getVittimeDifensore() == 2);
    }
}
