package it.engsoft.risiko.data.model;

import it.engsoft.risiko.data.repository.MappaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ObiettiviTest {
    @Autowired
    MappaRepository mappaRepository;

    @Test
    void testConqContinenti() {
        Partita partita = init();

        List <Continente> continenti = new ArrayList<>();
        continenti.add(partita.getMappa().getContinenti().get(0));
        continenti.add(partita.getMappa().getContinenti().get(1));

        ConqContinenti obiettivoConqContinenti = new ConqContinenti(
                continenti,
                0,
                partita.getMappa()
        );
        partita.getGiocatoreAttivo().setObiettivo(obiettivoConqContinenti);
        partita.getMappa().getContinenti().get(0).getStati().forEach(Stato -> Stato.setProprietario(partita.getGiocatoreAttivo()));
        partita.getMappa().getContinenti().get(1).getStati().forEach(Stato -> Stato.setProprietario(partita.getGiocatoreAttivo()));

        assertNotNull(partita.getGiocatoreAttivo().getObiettivo().getDescrizione());
        assertTrue(partita.getGiocatoreAttivo().getObiettivo().raggiunto(partita.getGiocatoreAttivo()));
        assertTrue(partita.getGiocatoreAttivo().obRaggiunto());
    }

    @Test
    void testConqGiocatore() {
        Partita partita = init();

        ConqTerritori obiettivoConqTerritori = new ConqTerritori(
                30,
                1
        );
        Giocatore target = null;
        for (int i = 0; i < partita.getGiocatori().size(); i++) {
            if(!partita.getGiocatori().get(i).equals(partita.getGiocatoreAttivo()))
                target = partita.getGiocatori().get(i);
        }
        assertNotNull(target);
        ConqGiocatore obiettivoConqGiocatore = new ConqGiocatore(
                target,
                obiettivoConqTerritori
        );

        // test Conquista Giocatore obiettivo primario
        partita.getGiocatoreAttivo().setObiettivo(obiettivoConqGiocatore);
        List<Stato> stati = new ArrayList<>(target.getStati());

        for (Stato stato : stati) target.rimuoviStato(stato);
        assertTrue(target.isEliminato());
        target.setUccisore(partita.getGiocatoreAttivo());

        assertNotNull(partita.getGiocatoreAttivo().getObiettivo().getDescrizione());
        assertTrue(partita.getGiocatoreAttivo().getObiettivo().raggiunto(partita.getGiocatoreAttivo()));
        assertTrue(partita.getGiocatoreAttivo().obRaggiunto());
    }

    @Test
    void testConqTerritori() {
        Partita partita = init();

        ConqTerritori obiettivoConqTerritori = new ConqTerritori(
                30,
                1
        );
        Giocatore target = null;
        for (int i = 0; i < partita.getGiocatori().size(); i++) {
            if (!partita.getGiocatori().get(i).equals(partita.getGiocatoreAttivo()))
                target = partita.getGiocatori().get(i);
        }
        assertNotNull(target);
        ConqGiocatore obiettivoConqGiocatore = new ConqGiocatore(
                target,
                obiettivoConqTerritori
        );

        Giocatore altro = null;
        for (int i = 0; i < partita.getGiocatori().size(); i++) {
            if (!partita.getGiocatori().get(i).equals(partita.getGiocatoreAttivo()))
                if (!partita.getGiocatori().get(i).equals(target))
                    altro = partita.getGiocatori().get(i);
        }
        assertNotNull(altro);

        // test ConqTerritori + ConqGiocatore obiettivo di riserva
        partita.getGiocatoreAttivo().setObiettivo(obiettivoConqGiocatore);
        List<Stato> stati = new ArrayList<>(target.getStati());

        for (Stato value : stati) {
            target.rimuoviStato(value);
            value.setProprietario(altro);
            altro.aggiungiStato(value);
        }
        assertTrue(target.isEliminato());
        target.setUccisore(altro);

        assertNotEquals(partita.getGiocatoreAttivo(), target.getUccisore());
        assertNotEquals(partita.getGiocatoreAttivo(), target);
        assertNotNull(partita.getGiocatoreAttivo().getObiettivo().getDescrizione());
        assertFalse(partita.getGiocatoreAttivo().getObiettivo().raggiunto(partita.getGiocatoreAttivo()));
        assertFalse(partita.getGiocatoreAttivo().obRaggiunto());

        Stato stato;
        for (int i = 0; i < 30; i++) {
            stato = partita.getMappa().getStati().get(i);
            stato.getProprietario().rimuoviStato(stato);
            stato.setProprietario(partita.getGiocatoreAttivo());
            partita.getGiocatoreAttivo().aggiungiStato(stato);
        }

        assertNotNull(partita.getGiocatoreAttivo().getObiettivo().getDescrizione());
        assertTrue(partita.getGiocatoreAttivo().getObiettivo().raggiunto(partita.getGiocatoreAttivo()));
        assertTrue(partita.getGiocatoreAttivo().obRaggiunto());
    }

    private Partita init () {
        Mappa mappa = mappaRepository.findById(1L).orElse(null);
        List<Giocatore> giocatori = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            giocatori.add(new Giocatore("Giocatore" + i));
        }

        Partita partita = new Partita(mappa, giocatori);
        partita.getMazzo().distribuisciCarte(partita.getGiocatori());
        partita.occupazioneInizialeTerritori();

        for (int i = 0; i < partita.getGiocatori().size(); i++)
            partita.getGiocatori().get(i).setArmateDisponibili(0);

        return partita;
    }
}
