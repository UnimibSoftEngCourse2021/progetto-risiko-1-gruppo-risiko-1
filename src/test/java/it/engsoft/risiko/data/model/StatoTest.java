package it.engsoft.risiko.data.model;

import it.engsoft.risiko.exceptions.ModelDataException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StatoTest {
    @Test
    void testModificaArmate() {
        Stato s = new Stato("s", null);

        s.aggiungiArmate(3);
        assertEquals(3, s.getArmate());

        s.rimuoviArmate(2);
        assertEquals(1, s.getArmate());

        try {
            s.rimuoviArmate(2);
            fail();
        } catch (ModelDataException ignored) {}
    }
}
