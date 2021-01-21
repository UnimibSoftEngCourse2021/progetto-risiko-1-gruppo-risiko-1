package it.engsoft.risiko.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.engsoft.risiko.dto.*;
import it.engsoft.risiko.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PartitaControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private HttpSession creaPartita() throws Exception {
        NuovoGiocoDTO nuovoGiocoDTO = new NuovoGiocoDTO(
                new ArrayList<>(Arrays.asList("marco", "pippo", "pluto")),
                1L,
                "COMPLETA",
                false
        );
        String nuovoGiocoJson = (new ObjectMapper()).writeValueAsString(nuovoGiocoDTO);

        return this.mockMvc.perform(post("/api/gioco")
                .contentType(MediaType.APPLICATION_JSON)
                .content(nuovoGiocoJson))
                .andExpect(status().isOk()).andReturn()
                .getRequest().getSession();
    }

    @Test
    void testNuovoGioco() throws Exception {
        HttpSession httpSession = creaPartita();

        assertNotNull(httpSession);
        assertNotNull(httpSession.getAttribute("partita"));

        Partita partita = (Partita) httpSession.getAttribute("partita");
        assertTrue(partita.getGiocatori().stream().anyMatch(g -> g.getNome().equals("marco")));
        assertTrue(partita.getGiocatori().stream().anyMatch(g -> g.getNome().equals("pippo")));
        assertTrue(partita.getGiocatori().stream().anyMatch(g -> g.getNome().equals("pluto")));

        assertEquals(partita.getMappa().getId(), 1L);
    }

    @Test
    void testMossaIllegale() throws Exception {
        HttpSession httpSession = creaPartita();

        this.mockMvc.perform(post("/api/fine-turno").sessionAttr("partita", httpSession.getAttribute("partita")))
                .andExpect(status().isForbidden()).andReturn().getRequest().getSession();
        assertNotNull(httpSession);
        assertNotNull(httpSession.getAttribute("partita"));
    }

    @Test
    void testRinforzi() throws Exception {
        HttpSession httpSession = creaPartita();
        assertNotNull(httpSession);
        assertNotNull(httpSession.getAttribute("partita"));
        Partita partita = (Partita) httpSession.getAttribute("partita");
        assertNotNull(partita);

        // rinforzo iniziale
        HashMap<String, Integer> rinforzo = new HashMap<>();
        rinforzo.put(partita.getGiocatoreAttivo().getStati().get(0).getId().toString(),
                Math.min(partita.getGiocatoreAttivo().getTruppeDisponibili(), 3));
        RinforzoDTO rinforzoDTO = new RinforzoDTO(
                partita.getGiocatoreAttivo().getNome(),
                rinforzo
        );
        assertEquals(partita.getGiocatoreAttivo().getNome(), rinforzoDTO.getGiocatore());

        String rinforzoJson = (new ObjectMapper()).writeValueAsString(rinforzoDTO);

        this.mockMvc.perform(post("/api/rinforzi")
                .sessionAttr("partita", httpSession.getAttribute("partita"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(rinforzoJson))
                .andExpect(status().isOk());

        assertTrue(partita.isFasePreparazione());
        assertNotEquals(partita.getGiocatoreAttivo().getNome(), rinforzoDTO.getGiocatore());
        assertNotEquals(0, partita.getGiocatoreAttivo().getTruppeDisponibili());

        // rinforzo normale
        for (int i = 0; i < partita.getGiocatori().size(); i++)
            partita.getGiocatori().get(i).setTruppeDisponibili(0);
        partita.setFasePreparazione(false);
        partita.iniziaPrimoTurno();

        this.mockMvc.perform(post("/api/inizia-turno")
                .sessionAttr("partita", httpSession.getAttribute("partita")))
                .andExpect(status().isOk());

        rinforzo.put(partita.getGiocatoreAttivo().getStati().get(0).getId().toString(),
                partita.getGiocatoreAttivo().getTruppeDisponibili());
        RinforzoDTO rinforzoDTO1 = new RinforzoDTO(
                partita.getGiocatoreAttivo().getNome(),
                rinforzo
        );
        String rinforzoJson1 = (new ObjectMapper()).writeValueAsString(rinforzoDTO1);

        this.mockMvc.perform(post("/api/rinforzi")
                .sessionAttr("partita", httpSession.getAttribute("partita"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(rinforzoJson1))
                .andExpect(status().isOk());

        assertFalse(partita.isFasePreparazione());
        assertEquals(partita.getGiocatoreAttivo().getNome(), rinforzoDTO.getGiocatore());
        assertEquals(0, partita.getGiocatoreAttivo().getTruppeDisponibili());
    }

    @Test
    void testIniziaTurno() throws Exception {
        HttpSession httpSession = creaPartita();
        assertNotNull(httpSession);
        assertNotNull(httpSession.getAttribute("partita"));
        Partita partita = (Partita) httpSession.getAttribute("partita");

        for (int i = 0; i < partita.getGiocatori().size(); i++)
            partita.getGiocatori().get(i).setTruppeDisponibili(0);
        partita.setFasePreparazione(false);
        partita.iniziaPrimoTurno();

        this.mockMvc.perform(post("/api/inizia-turno")
                .sessionAttr("partita", httpSession.getAttribute("partita")))
                .andExpect(status().isOk());
    }

    @Test
    void testGiocaTris() throws Exception {
        HttpSession httpSession = creaPartita();
        assertNotNull(httpSession);
        assertNotNull(httpSession.getAttribute("partita"));
        Partita partita = (Partita) httpSession.getAttribute("partita");

        for (int i = 0; i < partita.getGiocatori().size(); i++)
            partita.getGiocatori().get(i).setTruppeDisponibili(0);
        partita.setFasePreparazione(false);
        partita.iniziaPrimoTurno();
        partita.getTurno().setFase(Turno.Fase.RINFORZI);


        CartaTerritorio uno = new CartaTerritorio(1, partita.getMappa().getStati().get(0), CartaTerritorio.Figura.CANNONE);
        CartaTerritorio due = new CartaTerritorio(2, partita.getMappa().getStati().get(1), CartaTerritorio.Figura.CANNONE);
        CartaTerritorio tre = new CartaTerritorio(3, partita.getMappa().getStati().get(2), CartaTerritorio.Figura.CANNONE);
        partita.getGiocatoreAttivo().aggiungiCartaTerritorio(uno);
        partita.getGiocatoreAttivo().aggiungiCartaTerritorio(due);
        partita.getGiocatoreAttivo().aggiungiCartaTerritorio(tre);

        List<Integer> tris = new ArrayList<>();
        tris.add(uno.getId());
        tris.add(due.getId());
        tris.add(tre.getId());

        TrisDTO trisDTO = new TrisDTO(
                partita.getGiocatoreAttivo().getNome(),
                tris
        );

        String trisJson = (new ObjectMapper()).writeValueAsString(trisDTO);

        this.mockMvc.perform(post("/api/tris")
                .sessionAttr("partita", httpSession.getAttribute("partita"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(trisJson))
                .andExpect(status().isOk());
    }

    @Test
    void testCombattimento() throws Exception {
        HttpSession httpSession = creaPartita();
        assertNotNull(httpSession);
        assertNotNull(httpSession.getAttribute("partita"));
        Partita partita = (Partita) httpSession.getAttribute("partita");

        for (int i = 0; i < partita.getGiocatori().size(); i++)
            partita.getGiocatori().get(i).setTruppeDisponibili(0);
        partita.setFasePreparazione(false);
        partita.iniziaPrimoTurno();
        partita.getTurno().setFase(Turno.Fase.RINFORZI);
        partita.getTurno().setCombattimentoInCorso(null);
        partita.getGiocatoreAttivo().getStati().get(0).aggiungiArmate(3);

        Stato attaccante = partita.getGiocatoreAttivo().getStati().get(0);
        Stato difensore = partita.getGiocatoreAttivo().getStati().get(0).getConfinanti().get(0);
        AttaccoDTO attaccoDTO = new AttaccoDTO(
                partita.getGiocatoreAttivo().getNome(),
                attaccante.getId(),
                difensore.getId(),
                3
        );

        String attaccoJson = (new ObjectMapper()).writeValueAsString(attaccoDTO);

        if (attaccante.getProprietario().equals(difensore.getProprietario())) {
            this.mockMvc.perform(post("/api/attacco")
                    .sessionAttr("partita", httpSession.getAttribute("partita"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(attaccoJson))
                    .andExpect(status().isForbidden());
        } else {
            this.mockMvc.perform(post("/api/attacco")
                    .sessionAttr("partita", httpSession.getAttribute("partita"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(attaccoJson))
                    .andExpect(status().isOk());

            assertNotNull(partita.getTurno().getCombattimentoInCorso());

            partita.getTurno().getCombattimentoInCorso().getStatoDifensore().aggiungiArmate(2);
            DifesaDTO difesaDTO = new DifesaDTO(
                    partita.getTurno().getCombattimentoInCorso().getStatoDifensore().getProprietario().getNome(),
                    3
            );
            String difesaJson = (new ObjectMapper()).writeValueAsString(difesaDTO);
            this.mockMvc.perform(post("/api/difesa")
                    .sessionAttr("partita", httpSession.getAttribute("partita"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(difesaJson))
                    .andExpect(status().isOk());
        }
    }

    @Test
    public void testSpostamento() throws Exception {
        HttpSession httpSession = creaPartita();
        assertNotNull(httpSession);
        assertNotNull(httpSession.getAttribute("partita"));
        Partita partita = (Partita) httpSession.getAttribute("partita");

        for (int i = 0; i < partita.getGiocatori().size(); i++)
            partita.getGiocatori().get(i).setTruppeDisponibili(0);
        partita.setFasePreparazione(false);
        partita.iniziaPrimoTurno();
        partita.getTurno().setFase(Turno.Fase.RINFORZI);

        assertNull(partita.getTurno().getCombattimentoInCorso());

        partita.getGiocatoreAttivo().getStati().get(0).aggiungiArmate(4);
        Stato partenza = partita.getGiocatoreAttivo().getStati().get(0);
        Stato arrivo = partita.getGiocatoreAttivo().getStati().get(0).getConfinanti().get(0);
        SpostamentoDTO spostamentoDTO = new SpostamentoDTO(
                partita.getGiocatoreAttivo().getNome(),
                partenza.getId(),
                arrivo.getId(),
                4
        );
        String spostamentoJson = (new ObjectMapper()).writeValueAsString(spostamentoDTO);

        if (partenza.getProprietario().equals(arrivo.getProprietario())) {
            this.mockMvc.perform(post("/api/spostamento")
                    .sessionAttr("partita", httpSession.getAttribute("partita"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(spostamentoJson))
                    .andExpect(status().isOk());
        } else {
            this.mockMvc.perform(post("/api/spostamento")
                    .sessionAttr("partita", httpSession.getAttribute("partita"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(spostamentoJson))
                    .andExpect(status().isForbidden());
        }
    }

    @Test
    public void testFineTurno() throws Exception {
        HttpSession httpSession = creaPartita();
        assertNotNull(httpSession);
        assertNotNull(httpSession.getAttribute("partita"));
        Partita partita = (Partita) httpSession.getAttribute("partita");

        for (int i = 0; i < partita.getGiocatori().size(); i++)
            partita.getGiocatori().get(i).setTruppeDisponibili(0);
        partita.setFasePreparazione(false);
        partita.iniziaPrimoTurno();
        partita.getTurno().setFase(Turno.Fase.RINFORZI);

        assertNull(partita.getTurno().getCombattimentoInCorso());

        this.mockMvc.perform(post("/api/fine-turno")
                .sessionAttr("partita", httpSession.getAttribute("partita")))
                .andExpect(status().isOk());
    }
}

