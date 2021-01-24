package it.engsoft.risiko.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.engsoft.risiko.rest.DTO.*;
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
        NuovoGiocoRequest nuovoGiocoRequest = new NuovoGiocoRequest(
                new ArrayList<>(Arrays.asList("marco", "pippo", "pluto")),
                1L,
                "COMPLETA",
                false
        );
        String nuovoGiocoJson = (new ObjectMapper()).writeValueAsString(nuovoGiocoRequest);

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
                Math.min(partita.getGiocatoreAttivo().getArmateDisponibili(), 3));
        RinforzoRequest rinforzoRequest = new RinforzoRequest(
                partita.getGiocatoreAttivo().getNome(),
                rinforzo
        );

        assertEquals(partita.getGiocatoreAttivo().getNome(), rinforzoRequest.getGiocatore());

        String rinforzoJson = (new ObjectMapper()).writeValueAsString(rinforzoRequest);

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
        assertNotEquals(partita.getGiocatoreAttivo().getNome(), rinforzoRequest.getGiocatore());
        assertNotEquals(0, partita.getGiocatoreAttivo().getArmateDisponibili());

        // rinforzo normale
        for (int i = 0; i < partita.getGiocatori().size(); i++)
            partita.getGiocatori().get(i).setArmateDisponibili(0);
        partita.setNuovoGiocatoreAttivoPreparazione();



        this.mockMvc.perform(post("/api/inizia-turno")
                .sessionAttr("partita", httpSession.getAttribute("partita")))
                .andExpect(status().isOk());

        rinforzo.put(partita.getGiocatoreAttivo().getStati().get(0).getId().toString(),
                partita.getGiocatoreAttivo().getArmateDisponibili());
        RinforzoRequest rinforzoRequest1 = new RinforzoRequest(
                partita.getGiocatoreAttivo().getNome(),
                rinforzo
        );
        String rinforzoJson1 = (new ObjectMapper()).writeValueAsString(rinforzoRequest1);

        this.mockMvc.perform(post("/api/rinforzi")
                .sessionAttr("partita", httpSession.getAttribute("partita"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(rinforzoJson1))
                .andExpect(status().isOk());

        assertFalse(partita.isFasePreparazione());
        assertEquals(partita.getGiocatoreAttivo().getNome(), rinforzoRequest.getGiocatore());
        assertEquals(0, partita.getGiocatoreAttivo().getArmateDisponibili());
    }

    @Test
    void testIniziaTurno() throws Exception {
        HttpSession httpSession = creaPartita();
        assertNotNull(httpSession);
        assertNotNull(httpSession.getAttribute("partita"));
        Partita partita = (Partita) httpSession.getAttribute("partita");

        for (int i = 0; i < partita.getGiocatori().size(); i++)
            partita.getGiocatori().get(i).setArmateDisponibili(0);
        partita.setNuovoGiocatoreAttivoPreparazione();

        // partita null
        this.mockMvc.perform(post("/api/inizia-turno"))
                .andExpect(status().isForbidden());

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
            partita.getGiocatori().get(i).setArmateDisponibili(0);
        partita.setNuovoGiocatoreAttivoPreparazione();
        partita.setFaseTurno(Partita.FaseTurno.RINFORZI);

        assertFalse(partita.isFasePreparazione());
        assertEquals(Partita.FaseTurno.RINFORZI, partita.getFaseTurno());


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
            partita.getGiocatori().get(i).setArmateDisponibili(0);
        partita.setNuovoGiocatoreAttivoPreparazione();
        partita.setFaseTurno(Partita.FaseTurno.RINFORZI);
        partita.setCombattimento(null);

        assertFalse(partita.isFasePreparazione());
        assertNotEquals(Partita.FaseTurno.SPOSTAMENTO, partita.getFaseTurno());
        assertEquals(0, partita.getGiocatoreAttivo().getArmateDisponibili());
        assertNull(partita.getCombattimento());

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

        assertNotNull(partita.getCombattimento());
        assertTrue(partita.getCombattimento().getStatoAttaccante().getArmate() >
                attaccoDTO.getArmate());
        assertEquals(Partita.FaseTurno.COMBATTIMENTI, partita.getFaseTurno());

        // attaccante e difensore hanno lo stesso proprietario
        for (int i = 0; i < partita.getGiocatori().size(); i++)
            partita.getGiocatori().get(i).setArmateDisponibili(0);
        partita.nuovoTurno();
        partita.setFaseTurno(Partita.FaseTurno.RINFORZI);
        partita.setCombattimento(null);

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
        assertNull(partita.getCombattimento());
    }

    @Test
    void testDifesa() throws Exception {
        HttpSession httpSession = creaPartita();
        assertNotNull(httpSession);
        assertNotNull(httpSession.getAttribute("partita"));
        Partita partita = (Partita) httpSession.getAttribute("partita");

        for (int i = 0; i < partita.getGiocatori().size(); i++)
            partita.getGiocatori().get(i).setArmateDisponibili(0);
        partita.setNuovoGiocatoreAttivoPreparazione();
        partita.setFaseTurno(Partita.FaseTurno.RINFORZI);
        partita.setCombattimento(null);

        assertFalse(partita.isFasePreparazione());
        assertNotEquals(Partita.FaseTurno.SPOSTAMENTO, partita.getFaseTurno());
        assertEquals(0, partita.getGiocatoreAttivo().getArmateDisponibili());
        assertNull(partita.getCombattimento());

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

        DifesaRequest difesaRequest = new DifesaRequest(
                difensore.getProprietario().getNome(),
                1
        );

        assertEquals(partita.getGiocatoreAttivo().getNome(), attaccoDTO.getGiocatore());
        assertEquals(partita.getGiocatoreAttivo(), attaccante.getProprietario());

        String attaccoJson = (new ObjectMapper()).writeValueAsString(attaccoDTO);
        String difesaJson = (new ObjectMapper()).writeValueAsString(difesaRequest);

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

        assertNotNull(partita.getCombattimento());
        assertTrue(partita.getCombattimento().getStatoAttaccante().getArmate() >
                attaccoDTO.getArmate());
        assertEquals(Partita.FaseTurno.COMBATTIMENTI, partita.getFaseTurno());

        this.mockMvc.perform(post("/api/difesa")
                .sessionAttr("partita", httpSession.getAttribute("partita"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(difesaJson))
                .andExpect(status().isOk());

        if (partita.getCombattimento() != null) {
            assertEquals(attaccante.getProprietario(), difensore.getProprietario());
            if (difensore.getProprietario().isEliminato())
                assertEquals(attaccante.getProprietario(), difensore.getProprietario().getUccisore());
        } else
            assertNotEquals(attaccante.getProprietario(), difensore.getProprietario());


        // attaccante e difensore hanno lo stesso proprietario
        for (int i = 0; i < partita.getGiocatori().size(); i++)
            partita.getGiocatori().get(i).setArmateDisponibili(0);
        partita.nuovoTurno();
        partita.setFaseTurno(Partita.FaseTurno.RINFORZI);
        partita.setCombattimento(null);

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

        DifesaRequest difesaRequest1 = new DifesaRequest(
                difensore.getProprietario().getNome(),
                3
        );

        String attaccoJson1 = (new ObjectMapper()).writeValueAsString(attaccoDTO1);
        String difesaJson1 = (new ObjectMapper()).writeValueAsString(difesaRequest1);
        assertEquals(attaccante.getProprietario(), difensore.getProprietario());
        this.mockMvc.perform(post("/api/attacco")
                .sessionAttr("partita", httpSession.getAttribute("partita"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(attaccoJson1))
                .andExpect(status().isForbidden());
        assertNull(partita.getCombattimento());

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
            partita.getGiocatori().get(i).setArmateDisponibili(0);
        partita.setNuovoGiocatoreAttivoPreparazione();
        partita.setFaseTurno(Partita.FaseTurno.RINFORZI);

        assertNull(partita.getCombattimento());
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
            partita.getGiocatori().get(i).setArmateDisponibili(0);
        partita.nuovoTurno();
        partita.setFaseTurno(Partita.FaseTurno.RINFORZI);

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
            partita.getGiocatori().get(i).setArmateDisponibili(0);
        partita.setNuovoGiocatoreAttivoPreparazione();
        partita.setFaseTurno(Partita.FaseTurno.RINFORZI);
        partita.setCombattimento(null);

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

        DifesaRequest difesaRequest = new DifesaRequest(
                difensore.getProprietario().getNome(),
                1
        );

        String attaccoJson = (new ObjectMapper()).writeValueAsString(attaccoDTO);
        String difesaJson = (new ObjectMapper()).writeValueAsString(difesaRequest);

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

        if (partita.getCombattimento() != null) {
            SpostamentoDTO spostamentoDTO = new SpostamentoDTO(
                    partita.getGiocatoreAttivo().getNome(),
                    attaccante.getId(),
                    difensore.getId(),
                    partita.getCombattimento().getArmateAttaccante()
            );
            String spostamentoJson = (new ObjectMapper()).writeValueAsString(spostamentoDTO);

            this.mockMvc.perform(post("/api/spostamento")
                    .sessionAttr("partita", httpSession.getAttribute("partita"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(spostamentoJson))
                    .andExpect(status().isOk());

            assertNull(partita.getCombattimento());
        }
    }

    @Test
    void testFineTurno() throws Exception {
        HttpSession httpSession = creaPartita();
        assertNotNull(httpSession);
        assertNotNull(httpSession.getAttribute("partita"));
        Partita partita = (Partita) httpSession.getAttribute("partita");

        for (int i = 0; i < partita.getGiocatori().size(); i++)
            partita.getGiocatori().get(i).setArmateDisponibili(0);
        partita.setNuovoGiocatoreAttivoPreparazione();
        partita.setFaseTurno(Partita.FaseTurno.RINFORZI);
        partita.registraConquista();

        assertNull(partita.getCombattimento());
        assertEquals(0, partita.getGiocatoreAttivo().getArmateDisponibili());

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

