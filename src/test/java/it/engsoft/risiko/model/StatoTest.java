package it.engsoft.risiko.model;

import it.engsoft.risiko.exceptions.ModelDataException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StatoTest {
    @Test
    void testModificaArmate() {
        Stato s = new Stato("s", null);

        s.aggiungiArmate(3);
        assertEquals(s.getArmate(), 3);

        s.rimuoviArmate(2);
        assertEquals(s.getArmate(), 1);

        try {
            s.rimuoviArmate(2);
            fail();
        } catch (ModelDataException ignored) {}
    }
}
