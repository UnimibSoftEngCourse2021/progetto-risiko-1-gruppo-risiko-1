package it.engsoft.risiko.model;

import it.engsoft.risiko.exceptions.ModelDataException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class GiocatoreTest {

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
}
