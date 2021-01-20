package it.engsoft.risiko.service;

import it.engsoft.risiko.Utils;
import it.engsoft.risiko.model.Giocatore;
import it.engsoft.risiko.model.Obiettivo;
import it.engsoft.risiko.model.Partita;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CarteObiettivoServiceTest {
    private final CarteObiettivoService carteObiettivoService;
    private final Utils utils;

    @Autowired
    public CarteObiettivoServiceTest(CarteObiettivoService carteObiettivoService, Utils utils) {
        this.carteObiettivoService = carteObiettivoService;
        this.utils = utils;
    }

    @Test
    public void testSetObiettivi() {
        Partita partita = utils.initPartita();

        // test obiettivi casuali
        carteObiettivoService.setObiettiviGiocatori(partita.getMappa(), partita.getGiocatori(), false);

        for(Giocatore giocatore : partita.getGiocatori())
            assertNotNull(giocatore.getObiettivo());

        // test obiettivo uguale
        carteObiettivoService.setObiettiviGiocatori(partita.getMappa(), partita.getGiocatori(), true);

        Obiettivo obiettivo = partita.getGiocatori().get(0).getObiettivo();
        for(Giocatore giocatore : partita.getGiocatori())
            assertEquals(obiettivo, giocatore.getObiettivo());
    }
}
