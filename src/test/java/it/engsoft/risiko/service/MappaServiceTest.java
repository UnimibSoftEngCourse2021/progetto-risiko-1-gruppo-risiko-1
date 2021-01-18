package it.engsoft.risiko.service;

import it.engsoft.risiko.model.Continente;
import it.engsoft.risiko.model.Mappa;
import it.engsoft.risiko.model.Partita;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MappaServiceTest {
    @Autowired
    private MappaService mappaService;

    @Test
    public void testGetMappa() {
        Mappa mappa = mappaService.getMappa(1L, Partita.Modalita.COMPLETA.toString());
        assertEquals(mappa.getNome(), "Risiko Test");

        assertEquals(mappa.getContinenti().size(), 6);
        assertEquals(mappa.getStati().size(), 42);
    }

    @Test
    public void testCompattamentoMappe() {
        Mappa mappaCompleta = mappaService.getMappa(1L, Partita.Modalita.COMPLETA.toString());
        Mappa mappaRidotta = mappaService.getMappa(1L, Partita.Modalita.RIDOTTA.toString());
        Mappa mappaVeloce = mappaService.getMappa(1L, Partita.Modalita.VELOCE.toString());

        assertEquals(mappaCompleta.getContinenti().size(), mappaRidotta.getContinenti().size());
        assertEquals(mappaRidotta.getContinenti().size(), mappaVeloce.getContinenti().size());

        for (int i = 0; i < mappaCompleta.getContinenti().size(); i++) {
            Continente continenteCompleta = mappaCompleta.getContinenti().get(i);
            Continente continenteRidotta = mappaRidotta.getContinenti().get(i);
            Continente continenteVeloce = mappaVeloce.getContinenti().get(i);

            assertTrue(continenteCompleta.getStati().size() > continenteRidotta.getStati().size());
            assertTrue(continenteRidotta.getStati().size() > continenteVeloce.getStati().size());
        }
    }
}
