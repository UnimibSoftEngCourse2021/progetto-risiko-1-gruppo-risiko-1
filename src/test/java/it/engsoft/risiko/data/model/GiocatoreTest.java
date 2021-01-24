package it.engsoft.risiko.data.model;

import it.engsoft.risiko.exceptions.ModelDataException;
import it.engsoft.risiko.data.repository.MappaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GiocatoreTest {
    @Autowired
    MappaRepository mappaRepository;

    @Test
    void testNomeVuoto() {
        try {
            Giocatore giocatore = new Giocatore("");
            fail("Non ha lanciato l'eccezione");
        } catch (ModelDataException ignored) { }
    }

    @Test
    void testCreazione() {
        Giocatore giocatore = new Giocatore("Pippo");
        assertEquals("Pippo", giocatore.getNome());
    }

    @Test
    void testGestioneStati() {
        Giocatore giocatore = new Giocatore("Pippo");
        Mappa mappa = mappaRepository.findById(1L).orElse(null);
        Stato stato1 = mappa.getStati().get(0);
        Stato stato2 = mappa.getStati().get(1);

        // aggiungi uno stato valido
        giocatore.aggiungiStato(stato1);
        assertEquals(1, giocatore.getStati().size());

        // aggiungi uno stato non valido
        try {
            giocatore.aggiungiStato(null);
            fail();
        } catch (ModelDataException ignored) { }

        // aggiungi uno stato già presente
        try {
            giocatore.aggiungiStato(stato1);
            fail();
        } catch (ModelDataException ignored) { }

        // aggiungi un altro stato
        giocatore.aggiungiStato(stato2);
        assertEquals(2, giocatore.getStati().size());

        // rimuovi stato presente
        giocatore.rimuoviStato(stato1);
        assertEquals(1, giocatore.getStati().size());

        // rimuovi stato non presente
        try {
            giocatore.rimuoviStato(stato1);
            fail();
        } catch (ModelDataException ignored) { }
    }

    @Test
    void testEliminato() {
        Giocatore giocatore = new Giocatore("Pippo");
        Mappa mappa = mappaRepository.findById(1L).orElse(null);
        Stato stato = mappa.getStati().get(0);

        giocatore.aggiungiStato(stato);
        assertFalse(giocatore.isEliminato());

        // se non ha stati allora è eliminato
        giocatore.rimuoviStato(stato);
        assertTrue(giocatore.isEliminato());
    }

    @Test
    void testEquals() {
        Giocatore gioc1 = new Giocatore("Pippo");
        Giocatore gioc2 = new Giocatore("Pluto");
        Giocatore gioc3 = new Giocatore("Pippo");

        assertNotEquals(gioc2, gioc1);

        gioc1.setArmateDisponibili(2);
        gioc3.setArmateDisponibili(1);
        assertEquals(gioc1, gioc3);
    }

    @Test
    void testGestioneArmate() {
        Giocatore g = new Giocatore("Pippo");
        g.setArmateDisponibili(10);
        assertEquals(10, g.getArmateDisponibili());

        try {
            g.setArmateDisponibili(-10);
            fail();
        } catch (ModelDataException ignored) { }

        g.modificaTruppeDisponibili(10);
        assertEquals(20, g.getArmateDisponibili());

        g.modificaTruppeDisponibili(-5);
        assertEquals(15, g.getArmateDisponibili());

        try {
            g.modificaTruppeDisponibili(-16);
            fail();
        } catch (ModelDataException ignored) { }
    }

    @Test
    void testUccisore() {
        Giocatore pippo = new Giocatore("Pippo");
        Giocatore pluto = new Giocatore("Pluto");
        Stato s = new Stato();
        pippo.aggiungiStato(s);

        // non eliminato
        try {
            pippo.setUccisore(pluto);
            fail();
        } catch (ModelDataException ignored) {}

        // null
        try {
            pippo.setUccisore(null);
            fail();
        } catch (ModelDataException ignored) {}

        // se stesso
        try {
            pippo.setUccisore(pippo);
            fail();
        } catch (ModelDataException ignored) {}

        // valido
        pippo.rimuoviStato(s);
        pippo.setUccisore(pluto);
        assertEquals(pluto, pippo.getUccisore());
    }

}
