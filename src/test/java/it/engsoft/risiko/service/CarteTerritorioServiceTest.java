package it.engsoft.risiko.service;

import it.engsoft.risiko.Utils;
import it.engsoft.risiko.exceptions.MossaIllegaleException;
import it.engsoft.risiko.data.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class CarteTerritorioServiceTest {
    private final CarteTerritorioService carteTerritorioService;
    private final Utils utils;

    @Autowired
    public CarteTerritorioServiceTest(CarteTerritorioService carteTerritorioService, Utils utils) {
        this.carteTerritorioService = carteTerritorioService;
        this.utils = utils;
    }

    @Test
    void testCreazioneMazzo() {
        Partita partita = init();

        assertEquals(44, partita.getMazzo().size());
        assertEquals(8, partita.getGiocatori().get(0).getStati().size(), 1);
    }

    @Test
    void testPesca() {
        Partita partita = init();

        CartaTerritorio cartaValida = carteTerritorioService.pescaCarta(partita.getMazzo(), partita.getGiocatori().get(0));
        CartaTerritorio cartaNull = carteTerritorioService.pescaCarta(new ArrayList<CartaTerritorio>(), partita.getGiocatori().get(0));

        assertEquals(43, partita.getMazzo().size());
        assertEquals(1, partita.getGiocatori().get(0).getCarteTerritorio().size());
        assertNotNull(cartaValida);
        assertNull(cartaNull);
    }

    @Test
    void testValutaTrisCorretto() {
        Partita partita = init();
        Giocatore giocatore = partita.getGiocatori().get(0);

        List<Integer> tris = new ArrayList<>();

        CartaTerritorio carta1 = findCarta(partita.getMazzo(), CartaTerritorio.Figura.CANNONE);
        tris.add(carta1.getId());
        giocatore.aggiungiCartaTerritorio(carta1);

        CartaTerritorio carta2 = findCarta(partita.getMazzo(), CartaTerritorio.Figura.CANNONE);
        tris.add(carta2.getId());
        giocatore.aggiungiCartaTerritorio(carta2);

        CartaTerritorio carta3 = findCarta(partita.getMazzo(), CartaTerritorio.Figura.JOLLY);
        tris.add(carta3.getId());
        giocatore.aggiungiCartaTerritorio(carta3);

        carteTerritorioService.valutaTris(partita.getMazzo(), tris, giocatore);

        assertEquals(44, partita.getMazzo().size());
        assertEquals(0, giocatore.getCarteTerritorio().size());
        assertTrue(giocatore.getTruppeDisponibili() >= 12);
    }

    @Test
    void testValutaTrisErrato() {
        Partita partita = init();
        Giocatore giocatore = partita.getGiocatori().get(0);

        List<Integer> tris = new ArrayList<>();

        CartaTerritorio carta1 = findCarta(partita.getMazzo(), CartaTerritorio.Figura.CANNONE);
        tris.add(carta1.getId());
        giocatore.aggiungiCartaTerritorio(carta1);

        CartaTerritorio carta2 = findCarta(partita.getMazzo(), CartaTerritorio.Figura.CANNONE);
        tris.add(carta2.getId());
        giocatore.aggiungiCartaTerritorio(carta2);

        CartaTerritorio carta3 = findCarta(partita.getMazzo(), CartaTerritorio.Figura.FANTE);
        tris.add(carta3.getId());
        giocatore.aggiungiCartaTerritorio(carta3);

        // test tris non valido
        MossaIllegaleException e = assertThrows(MossaIllegaleException.class, () -> {
            carteTerritorioService.valutaTris(partita.getMazzo(), tris, giocatore);
        });
        assertEquals("Mossa illegale: tris non valido", e.getMessage());

        // test carta non appartenente al giocatore
        giocatore.rimuoviCartaTerritorio(carta3);
        e = assertThrows(MossaIllegaleException.class, () -> {
            carteTerritorioService.valutaTris(partita.getMazzo(), tris, giocatore);
        });
        assertEquals("Mossa illegale: le carte non appartengono al giocatore", e.getMessage());

        assertEquals(41, partita.getMazzo().size());
    }

    private Partita init() {
        Partita partita = utils.initPartita();
        carteTerritorioService.distribuisciCarte(partita);

        return partita;
    }

    private CartaTerritorio findCarta(List<CartaTerritorio> mazzo, CartaTerritorio.Figura figura) {
        CartaTerritorio carta = mazzo.stream()
                .filter(c -> c.getFigura() == figura)
                .findFirst()
                .get();

        mazzo.remove(carta);
        return carta;
    }
}
