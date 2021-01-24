package it.engsoft.risiko.service;

import it.engsoft.risiko.Utils;
import it.engsoft.risiko.rest.DTO.NuovaMappaDTO;
import it.engsoft.risiko.data.model.Continente;
import it.engsoft.risiko.data.model.Mappa;
import it.engsoft.risiko.data.model.Modalita;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MappaServiceTest {
    private final MappaService mappaService;
    private final Utils utils;

    @Autowired
    public MappaServiceTest(MappaService mappaService, Utils utils) {
        this.mappaService = mappaService;
        this.utils = utils;
    }

    @Test
    void testGetMappa() {
        Mappa mappa = mappaService.getMappa(1L, Modalita.COMPLETA);
        assertEquals("Risiko Test", mappa.getNome());

        assertEquals(6, mappa.getContinenti().size());
        assertEquals(42, mappa.getStati().size());
    }

    @Test
    void testCompattamentoMappe() {
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
    void testInserisciMappa() {
        // prendiamo la mappa standard, la formattiamo come dto e proviamo a reinserirla con un nome diverso
        Mappa mappaStandard = mappaService.getMappa(1L, Modalita.COMPLETA);
        NuovaMappaDTO nuovaMappa = utils.dtoFromMappa(mappaStandard, "Nuova mappa", "nuova desc", 2, 8);

        mappaService.nuovaMappa(nuovaMappa);

        assertEquals(2, mappaService.mappe().size());
        assertNotNull(mappaService.getMappa(2L, Modalita.COMPLETA));
    }
}
