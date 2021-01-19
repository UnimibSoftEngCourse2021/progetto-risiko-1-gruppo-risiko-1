package it.engsoft.risiko.model;

import it.engsoft.risiko.exceptions.ModelDataException;
import it.engsoft.risiko.repository.MappaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class GiocatoreTest {
    @Autowired
    MappaRepository mappaRepository;

    @Test
    public void testNomeVuoto() {
        try {
            Giocatore giocatore = new Giocatore("");
            fail("Non ha lanciato l'eccezione");
        } catch (ModelDataException ignored) { }
    }

    @Test
    public void testCreazione() {
        Giocatore giocatore = new Giocatore("Pippo");
        assertEquals(giocatore.getNome(), "Pippo");
    }

    @Test
    public void testGestioneStati() {
        Giocatore giocatore = new Giocatore("Pippo");
        Mappa mappa = mappaRepository.findById(1L).orElse(null);
        Stato stato1 = mappa.getStati().get(0);
        Stato stato2 = mappa.getStati().get(1);

        // aggiungi uno stato valido
        giocatore.aggiungiStato(stato1);
        assertEquals(giocatore.getStati().size(), 1);

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
        assertEquals(giocatore.getStati().size(), 2);

        // rimuovi stato presente
        giocatore.rimuoviStato(stato1);
        assertEquals(giocatore.getStati().size(), 1);

        // rimuovi stato non presente
        try {
            giocatore.rimuoviStato(stato1);
            fail();
        } catch (ModelDataException ignored) { }
    }

    @Test
    public void testEliminato() {
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
    public void testEquals() {
        Giocatore gioc1 = new Giocatore("Pippo");
        Giocatore gioc2 = new Giocatore("Pluto");
        Giocatore gioc3 = new Giocatore("Pippo");

        assertNotEquals(gioc2, gioc1);

        gioc1.setTruppeDisponibili(2);
        gioc3.setTruppeDisponibili(1);
        assertEquals(gioc3, gioc1);
    }

    @Test
    public void testGestioneArmate() {
        Giocatore g = new Giocatore("Pippo");
        g.setTruppeDisponibili(10);
        assertEquals(g.getTruppeDisponibili(), 10);

        try {
            g.setTruppeDisponibili(-10);
            fail();
        } catch (ModelDataException ignored) { }

        g.modificaTruppeDisponibili(10);
        assertEquals(g.getTruppeDisponibili(), 20);

        g.modificaTruppeDisponibili(-5);
        assertEquals(g.getTruppeDisponibili(), 15);

        try {
            g.modificaTruppeDisponibili(-16);
            fail();
        } catch (ModelDataException ignored) { }
    }

    @Test
    public void testUccisore() {
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
        assertEquals(pippo.getUccisore(), pluto);
    }

}
