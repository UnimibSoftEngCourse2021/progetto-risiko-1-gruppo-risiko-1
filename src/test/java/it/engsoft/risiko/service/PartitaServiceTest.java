package it.engsoft.risiko.service;

import it.engsoft.risiko.data.model.*;
import it.engsoft.risiko.http.dto.NuovoGiocoRequest;
import it.engsoft.risiko.exceptions.DatiErratiException;
import it.engsoft.risiko.exceptions.MossaIllegaleException;
import it.engsoft.risiko.http.dto.TrisDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PartitaServiceTest {
    private final PartitaService partitaService;

    @Autowired
    public PartitaServiceTest(PartitaService partitaService) {
        this.partitaService = partitaService;
    }

    @Test
    void testNuovoGioco() {
        ArrayList<String> giocatori = new ArrayList<>();
        giocatori.add("uno");
        giocatori.add("due");

        // test numero giocatori < minimo consentito
        NuovoGiocoRequest nuovoGiocoRequest1 = new NuovoGiocoRequest(giocatori, 1L, "COMPLETA", false);
        try {
            partitaService.nuovoGioco(nuovoGiocoRequest1);
            fail();
        } catch (DatiErratiException ignored) {
        }

        giocatori.add("tre");
        giocatori.add("quattro");
        giocatori.add("cinque");
        giocatori.add("sei");

        NuovoGiocoRequest nuovoGiocoRequest = new NuovoGiocoRequest(giocatori, 1L, "COMPLETA", false);
        Partita partita = partitaService.nuovoGioco(nuovoGiocoRequest);

        assertEquals(1L, partita.getMappa().getId());

        // test sulla creazione dei giocatori
        for (int i = 0; i < partita.getGiocatori().size(); i++) {
            assertNotNull(partita.getGiocatori().get(i));
            assertNotNull(partita.getGiocatori().get(i).getNome());
            assertNotNull(partita.getGiocatori().get(i).getObiettivo());
            assertNotNull(partita.getGiocatori().get(i).getCarteTerritorio());
            assertNotNull(partita.getGiocatori().get(i).getStati());
            assertNotEquals(0, partita.getGiocatori().get(i).getArmateDisponibili());
        }

        // test numero giocatori > massimo consentito
        giocatori.add("sette");
        nuovoGiocoRequest = new NuovoGiocoRequest(giocatori, 1L, "COMPLETA", false);
        try {
            partitaService.nuovoGioco(nuovoGiocoRequest);
            fail();
        } catch (DatiErratiException ignored) {
        }
    }

    @Test
    void testIniziaTurno() {
        ArrayList<String> giocatori = new ArrayList<>();
        giocatori.add("uno");
        giocatori.add("due");
        giocatori.add("tre");

        NuovoGiocoRequest nuovoGiocoRequest = new NuovoGiocoRequest(giocatori, 1L, "COMPLETA", false);
        Partita partita = partitaService.nuovoGioco(nuovoGiocoRequest);
        for(int i = 0; i < partita.getGiocatori().size(); i++) {
            partita.getGiocatori().get(i).setArmateDisponibili(0);
        }

        // test fase preparazione
        try {
            partitaService.iniziaTurno(partita);
            fail();
        } catch (MossaIllegaleException ignore) { }

        partita.setNuovoGiocatoreAttivoPreparazione();

        // test fase diversa da INIZIALIZZAZIONE
        partita.setFaseTurno(Partita.FaseTurno.RINFORZI);
        try {
            partitaService.iniziaTurno(partita);
            fail();
        } catch (MossaIllegaleException ignore) { }

        partita.setFaseTurno(Partita.FaseTurno.INIZIALIZZAZIONE);
        partitaService.iniziaTurno(partita);
        assertNotEquals(0, partita.getGiocatoreAttivo().getArmateDisponibili());
        assertEquals(Partita.FaseTurno.RINFORZI, partita.getFaseTurno());
    }

    @Test
    void testGiocaTris() {
        ArrayList<String> giocatori = new ArrayList<>();
        giocatori.add("uno");
        giocatori.add("due");
        giocatori.add("tre");

        NuovoGiocoRequest nuovoGiocoRequest = new NuovoGiocoRequest(giocatori, 1L, "COMPLETA", false);
        Partita partita = partitaService.nuovoGioco(nuovoGiocoRequest);
        for (int i = 0; i < partita.getGiocatori().size(); i++) {
            partita.getGiocatori().get(i).setArmateDisponibili(0);
        }

        CartaTerritorio carta1 = new CartaTerritorio(
                1,
                partita.getMappa().getStati().get(0),
                CartaTerritorio.Figura.CAVALLO);
        CartaTerritorio carta2 = new CartaTerritorio(
                2,
                partita.getMappa().getStati().get(1),
                CartaTerritorio.Figura.CAVALLO);
        CartaTerritorio carta3 = new CartaTerritorio(
                3,
                partita.getMappa().getStati().get(2),
                CartaTerritorio.Figura.CAVALLO);
        CartaTerritorio carta4 = new CartaTerritorio(
                4,
                partita.getMappa().getStati().get(3),
                CartaTerritorio.Figura.FANTE);

        List<Integer> carte = new ArrayList<>();
        carte.add(carta1.getId());
        carte.add(carta2.getId());
        carte.add(carta3.getId());

        TrisDTO trisDTO = new TrisDTO(
                partita.getGiocatoreAttivo().getNome(),
                carte);

        assertTrue(partita.isFasePreparazione());
        // test fase preparazione
        try {
            partitaService.giocaTris(trisDTO, partita);
            fail();
        } catch (MossaIllegaleException ignore) { }

        partita.setNuovoGiocatoreAttivoPreparazione();
        partita.setFaseTurno(Partita.FaseTurno.COMBATTIMENTI);

        assertFalse(partita.isFasePreparazione());

        // test non si e' in fase di rinforzo
        try {
            partitaService.giocaTris(trisDTO, partita);
            fail();
        } catch (MossaIllegaleException ignore) { }

        partita.setFaseTurno(Partita.FaseTurno.RINFORZI);

        Giocatore giocatore = null;
        for (int i = 0; i < partita.getGiocatori().size(); i++) {
            if (!partita.getGiocatori().get(i).equals(partita.getGiocatoreAttivo()))
                giocatore = partita.getGiocatori().get(i);
        }
        assertNotNull(giocatore);
        TrisDTO trisDTO1 = new TrisDTO(
                giocatore.getNome(),
                carte);
        try {
            partitaService.giocaTris(trisDTO1, partita);
            fail();
        } catch (MossaIllegaleException ignore) { }

        carte.add(carta4.getId());

        // test numero carte diverso da 3
        try {
            partitaService.giocaTris(trisDTO, partita);
            fail();
        } catch (DatiErratiException ignore) { }
    }

    @Test
    void testFineTurno() {
        ArrayList<String> giocatori = new ArrayList<>();
        giocatori.add("uno");
        giocatori.add("due");
        giocatori.add("tre");

        NuovoGiocoRequest nuovoGiocoRequest = new NuovoGiocoRequest(giocatori, 1L, "COMPLETA", false);
        Partita partita = partitaService.nuovoGioco(nuovoGiocoRequest);
        for (int i = 0; i < partita.getGiocatori().size(); i++) {
            partita.getGiocatori().get(i).setArmateDisponibili(0);
        }

        assertTrue(partita.isFasePreparazione());
        // test fase preparazione
        try {
            partitaService.fineTurno(partita);
            fail();
        } catch (MossaIllegaleException ignore) { }

        partita.setNuovoGiocatoreAttivoPreparazione();
        assertFalse(partita.isFasePreparazione());

        partita.getGiocatoreAttivo().setArmateDisponibili(1);
        // test armate diverse da zero
        try {
            partitaService.fineTurno(partita);
            fail();
        } catch (MossaIllegaleException ignore) { }
    }
}
