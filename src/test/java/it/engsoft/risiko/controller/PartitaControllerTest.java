package it.engsoft.risiko.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.engsoft.risiko.rest.dto.*;
import it.engsoft.risiko.data.model.*;
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
class PartitaControllerTest {
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

        assertEquals(1L, partita.getMappa().getId());
    }

    @Test
    void testMossaIllegale() throws Exception {
        HttpSession httpSession = creaPartita();

        this.mockMvc.perform(post("/api/fine-turno")
                .sessionAttr("partita", httpSession.getAttribute("partita")))
                .andExpect(status().isForbidden())
                .andReturn().getRequest().getSession();
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

        // partita null
        this.mockMvc.perform(post("/api/rinforzi")
                .contentType(MediaType.APPLICATION_JSON)
                .content(rinforzoJson))
                .andExpect(status().isForbidden());

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

        // partita null
        this.mockMvc.perform(post("/api/inizia-turno"))
                .andExpect(status().isForbidden());

        this.mockMvc.perform(post("/api/inizia-turno")
                .sessionAttr("partita", httpSession.getAttribute("partita")))
                .andExpect(status().isOk());

        assertNotNull(partita.getTurno());
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

        assertFalse(partita.isFasePreparazione());
        assertEquals(Turno.Fase.RINFORZI, partita.getTurno().getFase());


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

        assertEquals(3, trisDTO.getTris().size());
        assertEquals(partita.getGiocatoreAttivo().getNome(), trisDTO.getGiocatore());

        String trisJson = (new ObjectMapper()).writeValueAsString(trisDTO);

        // partita null
        this.mockMvc.perform(post("/api/tris")
                .contentType(MediaType.APPLICATION_JSON)
                .content(trisJson))
                .andExpect(status().isForbidden());

        this.mockMvc.perform(post("/api/tris")
                .sessionAttr("partita", httpSession.getAttribute("partita"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(trisJson))
                .andExpect(status().isOk());
    }

    @Test
    void testAttacco() throws Exception {
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

        assertFalse(partita.isFasePreparazione());
        assertNotEquals(Turno.Fase.SPOSTAMENTO, partita.getTurno().getFase());
        assertEquals(0, partita.getGiocatoreAttivo().getTruppeDisponibili());
        assertNull(partita.getTurno().getCombattimentoInCorso());

        Stato attaccante = new Stato();
        Stato difensore = new Stato();
        for (int j=0; j< partita.getGiocatoreAttivo().getStati().size(); j++){
            for (int i=0; i< partita.getGiocatoreAttivo().getStati().get(j).getConfinanti().size(); i++) {
                if (!partita.getGiocatoreAttivo().getStati().get(j).getConfinanti().get(i).getProprietario().equals(
                        partita.getGiocatoreAttivo())){
                    attaccante = partita.getGiocatoreAttivo().getStati().get(j);
                    attaccante.aggiungiArmate(3);
                    difensore = partita.getGiocatoreAttivo().getStati().get(j).getConfinanti().get(i);
                    difensore.aggiungiArmate(2);
                }
            }
        }
        AttaccoDTO attaccoDTO = new AttaccoDTO(
                partita.getGiocatoreAttivo().getNome(),
                attaccante.getId(),
                difensore.getId(),
                3
        );

        assertEquals(partita.getGiocatoreAttivo().getNome(), attaccoDTO.getGiocatore());
        assertEquals(partita.getGiocatoreAttivo(), attaccante.getProprietario());

        String attaccoJson = (new ObjectMapper()).writeValueAsString(attaccoDTO);

        // partita null
        this.mockMvc.perform(post("/api/attacco")
                .contentType(MediaType.APPLICATION_JSON)
                .content(attaccoJson))
                .andExpect(status().isForbidden());

        // attacco valido
        assertNotEquals(attaccante.getProprietario(), difensore.getProprietario());
        this.mockMvc.perform(post("/api/attacco")
                .sessionAttr("partita", httpSession.getAttribute("partita"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(attaccoJson))
                .andExpect(status().isOk());

        assertNotNull(partita.getTurno().getCombattimentoInCorso());
        assertTrue(partita.getTurno().getCombattimentoInCorso().getStatoAttaccante().getArmate() >
                attaccoDTO.getArmate());
        assertEquals(Turno.Fase.COMBATTIMENTI, partita.getTurno().getFase());

        // attaccante e difensore hanno lo stesso proprietario
        for (int i = 0; i < partita.getGiocatori().size(); i++)
            partita.getGiocatori().get(i).setTruppeDisponibili(0);
        partita.nuovoTurno();
        partita.getTurno().setFase(Turno.Fase.RINFORZI);
        partita.getTurno().setCombattimentoInCorso(null);

        for (int j=0; j< partita.getGiocatoreAttivo().getStati().size(); j++){
            for (int i=0; i< partita.getGiocatoreAttivo().getStati().get(j).getConfinanti().size(); i++) {
                if (partita.getGiocatoreAttivo().getStati().get(j).getConfinanti().get(i).getProprietario().equals(
                        partita.getGiocatoreAttivo())){
                    attaccante = partita.getGiocatoreAttivo().getStati().get(j);
                    attaccante.aggiungiArmate(3);
                    difensore = partita.getGiocatoreAttivo().getStati().get(j).getConfinanti().get(i);
                    difensore.aggiungiArmate(2);
                }
            }
        }
        AttaccoDTO attaccoDTO1 = new AttaccoDTO(
                partita.getGiocatoreAttivo().getNome(),
                attaccante.getId(),
                difensore.getId(),
                3
        );

        String attaccoJson1 = (new ObjectMapper()).writeValueAsString(attaccoDTO1);
        assertEquals(attaccante.getProprietario(), difensore.getProprietario());
        this.mockMvc.perform(post("/api/attacco")
                .sessionAttr("partita", httpSession.getAttribute("partita"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(attaccoJson1))
                .andExpect(status().isForbidden());
        assertNull(partita.getTurno().getCombattimentoInCorso());
    }

    @Test
    void testDifesa() throws Exception {
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

        assertFalse(partita.isFasePreparazione());
        assertNotEquals(Turno.Fase.SPOSTAMENTO, partita.getTurno().getFase());
        assertEquals(0, partita.getGiocatoreAttivo().getTruppeDisponibili());
        assertNull(partita.getTurno().getCombattimentoInCorso());

        Stato attaccante = new Stato();
        Stato difensore = new Stato();
        for (int j=0; j< partita.getGiocatoreAttivo().getStati().size(); j++){
            for (int i=0; i< partita.getGiocatoreAttivo().getStati().get(j).getConfinanti().size(); i++) {
                if (!partita.getGiocatoreAttivo().getStati().get(j).getConfinanti().get(i).getProprietario().equals(
                        partita.getGiocatoreAttivo())){
                    attaccante = partita.getGiocatoreAttivo().getStati().get(j);
                    attaccante.aggiungiArmate(3);
                    difensore = partita.getGiocatoreAttivo().getStati().get(j).getConfinanti().get(i);
                    difensore.aggiungiArmate(2);
                }
            }
        }
        AttaccoDTO attaccoDTO = new AttaccoDTO(
                partita.getGiocatoreAttivo().getNome(),
                attaccante.getId(),
                difensore.getId(),
                3
        );

        DifesaDTO difesaDTO = new DifesaDTO(
                difensore.getProprietario().getNome(),
                1
        );

        assertEquals(partita.getGiocatoreAttivo().getNome(), attaccoDTO.getGiocatore());
        assertEquals(partita.getGiocatoreAttivo(), attaccante.getProprietario());

        String attaccoJson = (new ObjectMapper()).writeValueAsString(attaccoDTO);
        String difesaJson = (new ObjectMapper()).writeValueAsString(difesaDTO);

        // partita null
        this.mockMvc.perform(post("/api/difesa")
                .contentType(MediaType.APPLICATION_JSON)
                .content(attaccoJson))
                .andExpect(status().isForbidden());

        // attacco valido
        assertNotEquals(attaccante.getProprietario(), difensore.getProprietario());
        this.mockMvc.perform(post("/api/attacco")
                .sessionAttr("partita", httpSession.getAttribute("partita"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(attaccoJson))
                .andExpect(status().isOk());

        assertNotNull(partita.getTurno().getCombattimentoInCorso());
        assertTrue(partita.getTurno().getCombattimentoInCorso().getStatoAttaccante().getArmate() >
                attaccoDTO.getArmate());
        assertEquals(Turno.Fase.COMBATTIMENTI, partita.getTurno().getFase());

        this.mockMvc.perform(post("/api/difesa")
                .sessionAttr("partita", httpSession.getAttribute("partita"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(difesaJson))
                .andExpect(status().isOk());

        if (partita.getTurno().getCombattimentoInCorso() != null) {
            assertEquals(attaccante.getProprietario(), difensore.getProprietario());
            if (difensore.getProprietario().isEliminato())
                assertEquals(attaccante.getProprietario(), difensore.getProprietario().getUccisore());
        } else
            assertNotEquals(attaccante.getProprietario(), difensore.getProprietario());


        // attaccante e difensore hanno lo stesso proprietario
        for (int i = 0; i < partita.getGiocatori().size(); i++)
            partita.getGiocatori().get(i).setTruppeDisponibili(0);
        partita.nuovoTurno();
        partita.getTurno().setFase(Turno.Fase.RINFORZI);
        partita.getTurno().setCombattimentoInCorso(null);

        for (int j=0; j< partita.getGiocatoreAttivo().getStati().size(); j++){
            for (int i=0; i< partita.getGiocatoreAttivo().getStati().get(j).getConfinanti().size(); i++) {
                if (partita.getGiocatoreAttivo().getStati().get(j).getConfinanti().get(i).getProprietario().equals(
                        partita.getGiocatoreAttivo())){
                    attaccante = partita.getGiocatoreAttivo().getStati().get(j);
                    attaccante.aggiungiArmate(3);
                    difensore = partita.getGiocatoreAttivo().getStati().get(j).getConfinanti().get(i);
                    difensore.aggiungiArmate(2);
                }
            }
        }
        AttaccoDTO attaccoDTO1 = new AttaccoDTO(
                partita.getGiocatoreAttivo().getNome(),
                attaccante.getId(),
                difensore.getId(),
                3
        );

        DifesaDTO difesaDTO1 = new DifesaDTO(
                difensore.getProprietario().getNome(),
                3
        );

        String attaccoJson1 = (new ObjectMapper()).writeValueAsString(attaccoDTO1);
        String difesaJson1 = (new ObjectMapper()).writeValueAsString(difesaDTO1);
        assertEquals(attaccante.getProprietario(), difensore.getProprietario());
        this.mockMvc.perform(post("/api/attacco")
                .sessionAttr("partita", httpSession.getAttribute("partita"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(attaccoJson1))
                .andExpect(status().isForbidden());
        assertNull(partita.getTurno().getCombattimentoInCorso());

        this.mockMvc.perform(post("/api/difesa")
                .sessionAttr("partita", httpSession.getAttribute("partita"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(difesaJson1))
                .andExpect(status().isForbidden());
    }

    @Test
    void testSpostamento() throws Exception {
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
        assertFalse(partita.isFasePreparazione());

        Stato partenza = new Stato();
        Stato arrivo = new Stato();
        for (int j=0; j< partita.getGiocatoreAttivo().getStati().size(); j++){
            for (int i=0; i< partita.getGiocatoreAttivo().getStati().get(j).getConfinanti().size(); i++) {
                if (partita.getGiocatoreAttivo().getStati().get(j).getConfinanti().get(i).getProprietario().equals(
                        partita.getGiocatoreAttivo())){
                    partenza = partita.getGiocatoreAttivo().getStati().get(j);
                    partenza.aggiungiArmate(4);
                    arrivo = partita.getGiocatoreAttivo().getStati().get(j).getConfinanti().get(i);
                }
            }
        }
        SpostamentoDTO spostamentoDTO = new SpostamentoDTO(
                partita.getGiocatoreAttivo().getNome(),
                partenza.getId(),
                arrivo.getId(),
                4
        );
        String spostamentoJson = (new ObjectMapper()).writeValueAsString(spostamentoDTO);

        assertEquals(partita.getGiocatoreAttivo().getNome(), spostamentoDTO.getGiocatore());

        // partita null
        this.mockMvc.perform(post("/api/spostamento")
                .contentType(MediaType.APPLICATION_JSON)
                .content(spostamentoJson))
                .andExpect(status().isForbidden());

        // partenza e arrivo non appartengono allo stesso giocatore
        assertEquals(partenza.getProprietario(), arrivo.getProprietario());
        this.mockMvc.perform(post("/api/spostamento")
                .sessionAttr("partita", httpSession.getAttribute("partita"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(spostamentoJson))
                .andExpect(status().isOk());

        // partenza e arrivo non appartengono allo stesso giocatore
        for (int i = 0; i < partita.getGiocatori().size(); i++)
            partita.getGiocatori().get(i).setTruppeDisponibili(0);
        partita.nuovoTurno();
        partita.getTurno().setFase(Turno.Fase.RINFORZI);

        for (int j=0; j< partita.getGiocatoreAttivo().getStati().size(); j++){
            for (int i=0; i< partita.getGiocatoreAttivo().getStati().get(j).getConfinanti().size(); i++) {
                if (!partita.getGiocatoreAttivo().getStati().get(j).getConfinanti().get(i).getProprietario().equals(
                        partita.getGiocatoreAttivo())){
                    partenza = partita.getGiocatoreAttivo().getStati().get(j);
                    partenza.aggiungiArmate(4);
                    arrivo = partita.getGiocatoreAttivo().getStati().get(j).getConfinanti().get(i);
                }
            }
        }
        SpostamentoDTO spostamentoDTO1 = new SpostamentoDTO(
                partita.getGiocatoreAttivo().getNome(),
                partenza.getId(),
                arrivo.getId(),
                4
        );
        String spostamentoJson1 = (new ObjectMapper()).writeValueAsString(spostamentoDTO1);
        assertNotEquals(partenza.getProprietario(), arrivo.getProprietario());
        this.mockMvc.perform(post("/api/spostamento")
                .sessionAttr("partita", httpSession.getAttribute("partita"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(spostamentoJson1))
                .andExpect(status().isForbidden());
    }

    @Test
    void testSpostamentoAttacco() throws Exception {
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

        Stato attaccante = new Stato();
        Stato difensore = new Stato();
        for (int j=0; j< partita.getGiocatoreAttivo().getStati().size(); j++){
            for (int i=0; i< partita.getGiocatoreAttivo().getStati().get(j).getConfinanti().size(); i++) {
                if (!partita.getGiocatoreAttivo().getStati().get(j).getConfinanti().get(i).getProprietario().equals(
                        partita.getGiocatoreAttivo())){
                    attaccante = partita.getGiocatoreAttivo().getStati().get(j);
                    attaccante.aggiungiArmate(3);
                    difensore = partita.getGiocatoreAttivo().getStati().get(j).getConfinanti().get(i);
                    difensore.aggiungiArmate(2);
                }
            }
        }
        AttaccoDTO attaccoDTO = new AttaccoDTO(
                partita.getGiocatoreAttivo().getNome(),
                attaccante.getId(),
                difensore.getId(),
                3
        );

        DifesaDTO difesaDTO = new DifesaDTO(
                difensore.getProprietario().getNome(),
                1
        );

        String attaccoJson = (new ObjectMapper()).writeValueAsString(attaccoDTO);
        String difesaJson = (new ObjectMapper()).writeValueAsString(difesaDTO);

        this.mockMvc.perform(post("/api/attacco")
                .sessionAttr("partita", httpSession.getAttribute("partita"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(attaccoJson))
                .andExpect(status().isOk());

        this.mockMvc.perform(post("/api/difesa")
                .sessionAttr("partita", httpSession.getAttribute("partita"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(difesaJson))
                .andExpect(status().isOk());

        if (partita.getTurno().getCombattimentoInCorso() != null) {
            SpostamentoDTO spostamentoDTO = new SpostamentoDTO(
                    partita.getGiocatoreAttivo().getNome(),
                    attaccante.getId(),
                    difensore.getId(),
                    partita.getTurno().getCombattimentoInCorso().getArmateAttaccante()
            );
            String spostamentoJson = (new ObjectMapper()).writeValueAsString(spostamentoDTO);

            this.mockMvc.perform(post("/api/spostamento")
                    .sessionAttr("partita", httpSession.getAttribute("partita"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(spostamentoJson))
                    .andExpect(status().isOk());

            assertNull(partita.getTurno().getCombattimentoInCorso());
        }
    }

    @Test
    void testFineTurno() throws Exception {
        HttpSession httpSession = creaPartita();
        assertNotNull(httpSession);
        assertNotNull(httpSession.getAttribute("partita"));
        Partita partita = (Partita) httpSession.getAttribute("partita");

        for (int i = 0; i < partita.getGiocatori().size(); i++)
            partita.getGiocatori().get(i).setTruppeDisponibili(0);
        partita.setFasePreparazione(false);
        partita.iniziaPrimoTurno();
        partita.getTurno().setFase(Turno.Fase.RINFORZI);
        partita.getTurno().registraConquista();

        assertNull(partita.getTurno().getCombattimentoInCorso());
        assertEquals(0, partita.getGiocatoreAttivo().getTruppeDisponibili());

        Giocatore giocatoreAttivo = partita.getGiocatoreAttivo();

        // partita null
        this.mockMvc.perform(post("/api/fine-turno"))
                .andExpect(status().isForbidden());

        this.mockMvc.perform(post("/api/fine-turno")
                .sessionAttr("partita", httpSession.getAttribute("partita")))
                .andExpect(status().isOk());

        assertNotNull(giocatoreAttivo.getCarteTerritorio());
    }
}

