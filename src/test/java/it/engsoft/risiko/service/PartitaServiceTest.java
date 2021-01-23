package it.engsoft.risiko.service;

import it.engsoft.risiko.Utils;
import it.engsoft.risiko.rest.dto.NuovoGiocoDTO;
import it.engsoft.risiko.exceptions.DatiErratiException;
import it.engsoft.risiko.exceptions.MossaIllegaleException;
import it.engsoft.risiko.data.model.Partita;
import it.engsoft.risiko.data.model.Turno;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PartitaServiceTest {
    private final PartitaService partitaService;
    private final Utils utils;

    @Autowired
    public PartitaServiceTest(PartitaService partitaService, Utils utils) {
        this.partitaService = partitaService;
        this.utils = utils;
    }

    @Test
    void testNuovoGioco() {
        ArrayList<String> giocatori = new ArrayList<>();
        giocatori.add("uno");
        giocatori.add("due");

        // test numero giocatori < minimo consentito
        try {
            NuovoGiocoDTO nuovoGiocoDTO1 = new NuovoGiocoDTO(giocatori, 1L, "COMPLETA", false);
            partitaService.nuovoGioco(nuovoGiocoDTO1);
            fail();
        } catch (DatiErratiException ignored) {
        }

        giocatori.add("tre");
        giocatori.add("quattro");
        giocatori.add("cinque");
        giocatori.add("sei");

        NuovoGiocoDTO nuovoGiocoDTO = new NuovoGiocoDTO(giocatori, 1L, "COMPLETA", false);
        Partita partita = partitaService.nuovoGioco(nuovoGiocoDTO);

        assertEquals("COMPLETA", partita.getModalita().toString());
        assertEquals(1L, partita.getMappa().getId());

        // test sulla creazione dei giocatori
        for (int i = 0; i < partita.getGiocatori().size(); i++) {
            assertNotNull(partita.getGiocatori().get(i));
            assertNotNull(partita.getGiocatori().get(i).getNome());
            assertNotNull(partita.getGiocatori().get(i).getObiettivo());
            assertNotNull(partita.getGiocatori().get(i).getCarteTerritorio());
            assertNotNull(partita.getGiocatori().get(i).getStati());
            assertNotEquals(0, partita.getGiocatori().get(i).getTruppeDisponibili());
        }

        // test numero giocatori > massimo consentito
        try {
            giocatori.add("sette");
            nuovoGiocoDTO = new NuovoGiocoDTO(giocatori, 1L, "COMPLETA", false);
            partitaService.nuovoGioco(nuovoGiocoDTO);
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

        NuovoGiocoDTO nuovoGiocoDTO = new NuovoGiocoDTO(giocatori, 1L, "COMPLETA", false);
        Partita partita = partitaService.nuovoGioco(nuovoGiocoDTO);
        for(int i = 0; i < partita.getGiocatori().size(); i++) {
            partita.getGiocatori().get(i).setTruppeDisponibili(0);
        }

        // test fase preparazione
        try {
            partitaService.iniziaTurno(partita);
            fail();
        } catch (MossaIllegaleException ignore) { }

        partita.setNuovoGiocatoreAttivoPreparazione();

        // test fase diversa da INIZIALIZZAZIONE
        try {
            partita.getTurno().setFase(Turno.Fase.RINFORZI);
            partitaService.iniziaTurno(partita);
            fail();
        } catch (MossaIllegaleException ignore) { }

        partita.getTurno().setFase(Turno.Fase.INIZIALIZZAZIONE);
        partitaService.iniziaTurno(partita);
        assertNotEquals(0, partita.getGiocatoreAttivo().getTruppeDisponibili());
        assertEquals(Turno.Fase.RINFORZI, partita.getTurno().getFase());
    }

}
