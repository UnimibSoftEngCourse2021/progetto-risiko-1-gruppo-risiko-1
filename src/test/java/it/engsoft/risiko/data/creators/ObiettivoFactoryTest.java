package it.engsoft.risiko.data.creators;

import it.engsoft.risiko.Utils;
import it.engsoft.risiko.data.creators.ObiettivoFactory;
import it.engsoft.risiko.data.model.Giocatore;
import it.engsoft.risiko.data.model.Obiettivo;
import it.engsoft.risiko.data.model.Partita;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ObiettivoFactoryTest {
    private final Utils utils;

    @Autowired
    public ObiettivoFactoryTest(Utils utils) {
        this.utils = utils;
    }

    @Test
    void testSetObiettivi() {
        Partita partita = utils.initPartita();

        // test obiettivi casuali
        ObiettivoFactory obiettivoFactory = new ObiettivoFactory(partita.getMappa(), partita.getGiocatori(), false);
        partita.getGiocatori().forEach(g -> g.setObiettivo(obiettivoFactory.getNuovoObiettivo()));

        for(Giocatore giocatore : partita.getGiocatori())
            assertNotNull(giocatore.getObiettivo());

        // test obiettivo uguale
        ObiettivoFactory obiettivoFactory2 = new ObiettivoFactory(partita.getMappa(), partita.getGiocatori(), true);
        partita.getGiocatori().forEach(g -> g.setObiettivo(obiettivoFactory2.getNuovoObiettivo()));

        Obiettivo obiettivo = partita.getGiocatori().get(0).getObiettivo();
        for(Giocatore giocatore : partita.getGiocatori())
            assertEquals(obiettivo, giocatore.getObiettivo());
    }
}
