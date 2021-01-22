package it.engsoft.risiko.data.model;

import it.engsoft.risiko.exceptions.ModelDataException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class RinforzoTest {
    @Test
    void testCreazione() {
        Stato s = new Stato("Pippo", null);

        // nessuna eccezione
        Rinforzo rinforzo = new Rinforzo(s, 4);

        // stato null
        try {
            rinforzo = new Rinforzo(null, 4);
            fail();
        } catch (ModelDataException ignored) { }

        // armate non valide
        try {
            rinforzo = new Rinforzo(s, 0);
            fail();
        } catch (ModelDataException ignored) { }
    }

    @Test
    void testEsecuzione() {
        Stato s = new Stato("Pippo", null);
        s.aggiungiArmate(1);

        Rinforzo rinforzo = new Rinforzo(s, 4);
        rinforzo.esegui();

        assertEquals(s.getArmate(), 5);

        // prova ad eseguire lo stesso rinforzo più di una volta
        try {
            rinforzo.esegui();
            fail();
        } catch (ModelDataException ignored) {}
    }
}
