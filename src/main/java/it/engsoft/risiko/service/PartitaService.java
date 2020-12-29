package it.engsoft.risiko.service;

import it.engsoft.risiko.model.Partita;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

@Service
@Scope(value = WebApplicationContext.SCOPE_SESSION)
public class PartitaService {
    private Partita partita;

    public void nuovoGioco() {
        partita = new Partita();
    }


}
