package it.engsoft.risiko.data.model;

import it.engsoft.risiko.exceptions.ModelDataException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CartaTerritorioTest {

    @Test
    void testCreazione() {
        Stato stato = new Stato();

        // creazione corretta
        CartaTerritorio cartaTerritorio = new CartaTerritorio(1, stato, CartaTerritorio.Figura.CANNONE);
        assertEquals(1, cartaTerritorio.getId());
        assertEquals(stato, cartaTerritorio.getStatoRappresentato());
        assertEquals(CartaTerritorio.Figura.CANNONE, cartaTerritorio.getFigura());

        // id non valido
        try {
            cartaTerritorio = new CartaTerritorio(-1, stato, CartaTerritorio.Figura.CANNONE);
            fail();
        } catch (ModelDataException ignored) { }

        // figura null
        try {
            cartaTerritorio = new CartaTerritorio(1, stato, null);
            fail();
        } catch (ModelDataException ignored) { }

        // jolly con stato
        try {
            cartaTerritorio = new CartaTerritorio(1, stato, CartaTerritorio.Figura.JOLLY);
            fail();
        } catch (ModelDataException ignored) { }

        // non-jolly senza stato
        try {
            cartaTerritorio = new CartaTerritorio(1, null, CartaTerritorio.Figura.CAVALLO);
            fail();
        } catch (ModelDataException ignored) { }
    }
}
