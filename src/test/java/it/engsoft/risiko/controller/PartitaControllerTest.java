package it.engsoft.risiko.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.engsoft.risiko.dto.NuovoGiocoDTO;
import it.engsoft.risiko.model.Partita;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
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

        HttpSession httpSession = this.mockMvc.perform(post("/api/gioco")
                .contentType(MediaType.APPLICATION_JSON)
                .content(nuovoGiocoJson))
                .andExpect(status().isOk()).andReturn()
                .getRequest().getSession();
        return httpSession;
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

        this.mockMvc.perform(post("/api/fine-turno")
                .sessionAttr("partita", httpSession.getAttribute("partita")))
                .andExpect(status().isForbidden())
                .andReturn().getRequest().getSession();
        assertNotNull(httpSession);
        assertNotNull(httpSession.getAttribute("partita"));
    }
}
