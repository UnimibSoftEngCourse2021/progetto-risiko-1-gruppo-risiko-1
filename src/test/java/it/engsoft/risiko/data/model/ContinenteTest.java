package it.engsoft.risiko.data.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ContinenteTest {

    @Test
    void testProprietario() {
        Continente continente = new Continente("Prova", 3);
        Giocatore giocatore = new Giocatore("pippo");
        for (int i = 0; i < 6; i++) {
            Stato s = new Stato();
            s.setProprietario(giocatore);
            continente.getStati().add(s);
        }

        assertEquals(giocatore, continente.getProprietario());

        Giocatore altroGioc = new Giocatore("pluto");
        continente.getStati().get(0).setProprietario(altroGioc);
        assertNull(continente.getProprietario());
    }
}
