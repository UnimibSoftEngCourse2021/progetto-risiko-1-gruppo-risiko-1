package it.engsoft.risiko.service;

import it.engsoft.risiko.Utils;
import it.engsoft.risiko.dto.NuovaMappaDTO;
import it.engsoft.risiko.model.Continente;
import it.engsoft.risiko.model.Mappa;
import it.engsoft.risiko.model.Modalita;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MappaServiceTest {
    private final MappaService mappaService;
    private final Utils utils;

    @Autowired
    public MappaServiceTest(MappaService mappaService, Utils utils) {
        this.mappaService = mappaService;
        this.utils = utils;
    }

    @Test
    public void testGetMappa() {
        Mappa mappa = mappaService.getMappa(1L, Modalita.COMPLETA);
        assertEquals(mappa.getNome(), "Risiko Test");

        assertEquals(mappa.getContinenti().size(), 6);
        assertEquals(mappa.getStati().size(), 42);
    }

    @Test
    public void testCompattamentoMappe() {
        Mappa mappaCompleta = mappaService.getMappa(1L, Modalita.COMPLETA);
        Mappa mappaRidotta = mappaService.getMappa(1L, Modalita.RIDOTTA);
        Mappa mappaVeloce = mappaService.getMappa(1L, Modalita.VELOCE);

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

    @Test
    public void testInserisciMappa() {
        // prendiamo la mappa standard, la formattiamo come dto e proviamo a reinserirla con un nome diverso
        Mappa mappaStandard = mappaService.getMappa(1L, Modalita.COMPLETA);
        NuovaMappaDTO nuovaMappa = utils.dtoFromMappa(mappaStandard, "Nuova mappa", "nuova desc", 2, 8);

        mappaService.nuovaMappa(nuovaMappa);

        assertEquals(mappaService.mappe().size(), 2);
        assertNotNull(mappaService.getMappa(2L, Modalita.COMPLETA));
    }
}
