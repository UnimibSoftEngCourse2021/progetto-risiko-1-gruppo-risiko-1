package it.engsoft.risiko.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.containsString;

@SpringBootTest
@AutoConfigureMockMvc
class MappaControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetMappe() throws Exception{
        this.mockMvc.perform(get("/api/mappe")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Risiko Test")));
    }

    @Test
    void testGetMappaSingolaNonEsistente() throws Exception{
        this.mockMvc.perform(get("/api/mappe/2")).andExpect(status().isNotFound());
    }
}
