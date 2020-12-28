package it.engsoft.risiko.model;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;

@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION)
public class Partita {
    private final ArrayList<Giocatore> giocatori = new ArrayList<>();
    private Giocatore giocatoreAttivo;
    private Turno turno;
    private Mappa mappa;

    public void setGiocatori(Giocatore giocatori) {
        this.giocatori = giocatori;
    }
}
