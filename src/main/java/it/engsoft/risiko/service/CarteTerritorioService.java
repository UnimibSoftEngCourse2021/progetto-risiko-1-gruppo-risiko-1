package it.engsoft.risiko.service;

import it.engsoft.risiko.model.*;
import it.engsoft.risiko.exceptions.*;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CarteTerritorioService {
    /**
     * Genera il mazzo e distribuisce le carte ai giocatori.
     * Se il mazzo è già stato generato causa MossaIllegaleException.
     */
    public void distribuisciCarte(Partita partita) {
        List<CartaTerritorio> mazzo = partita.getMazzo();

        // Generazione mazzo
        generaMazzo(mazzo, partita.getMappa());

        // Distribuzione carte ai giocatori
        Collections.shuffle(mazzo);

        int g = 0; // indice giocatori

        // La carta 'c' viene assegnata al giocatore 'g'
        for (CartaTerritorio cartaTerritorio : mazzo) {
            if (cartaTerritorio.getFigura() != CartaTerritorio.Figura.JOLLY) {
                // Assegna lo stato rappresentato dalla carta al giocatore
                partita.getGiocatori().get(g).aggiungiStato(cartaTerritorio.getStatoRappresentato());
                cartaTerritorio.getStatoRappresentato().setProprietario(partita.getGiocatori().get(g));

                if (g == partita.getGiocatori().size() - 1)
                    g = 0;
                else
                    g++;
            }
        }
    }

    /**
     * Crea le carte e le aggiunge al mazzo.
     *
     * @param mappa: la mappa su cui si svolge la partita
     */
    private void generaMazzo(List<CartaTerritorio> mazzo, Mappa mappa) {
        // numStati viene utilizzato per contare gli stati e come ID per inizializzare le carte
        int numStati = 0;

        // conta degli stati e creazione le carte
        for (Continente continente : mappa.getContinenti()) {
            for (Stato stato : continente.getStati()) {
                numStati++;

                mazzo.add(new CartaTerritorio(numStati, stato));
            }
        }

        // aggiunta figure alle carte
        for (int i = 0; i < mazzo.size(); i++) {
            if (i % 3 == 0)
                mazzo.get(i).setFigura(CartaTerritorio.Figura.CANNONE);

            if (i % 3 == 1)
                mazzo.get(i).setFigura(CartaTerritorio.Figura.FANTE);

            if (i % 3 == 2)
                mazzo.get(i).setFigura(CartaTerritorio.Figura.CAVALLO);
        }

        int numJolly = Math.round(numStati / 20F);

        // aggiunge i jolly al mazzo
        for (int i = 0; i < numJolly; i++) {
            CartaTerritorio jolly = new CartaTerritorio(numStati + i, null);
            jolly.setFigura(CartaTerritorio.Figura.JOLLY);

            mazzo.add(jolly);
        }
    }

    public CartaTerritorio pescaCarta(List<CartaTerritorio> mazzo, Giocatore giocatore) {
        if(mazzo.size() == 0)
            return null;

        CartaTerritorio carta = mazzo.remove(0);
        giocatore.aggiungiCartaTerritorio(carta);

        return carta;
    }

    /**
     * Calcola il numero di armate che spettano al giocatore che gioca il tris.
     * Le carte giocate vengono rimesse nel mazzo.
     *
     * @param tris      : lista contenente gli id delle tre carte giocate
     * @param giocatore : il giocatore che gioca il tris
     * @return numero di armate che spettano al giocatore
     */
    public int valutaTris(List<CartaTerritorio> mazzo, List<Integer> tris, Giocatore giocatore) {
        CartaTerritorio a;
        CartaTerritorio b;
        CartaTerritorio c;

        try {
            a = giocatore.getCarteTerritorio().stream().filter(carta -> carta.getId() == tris.get(0)).findAny().get();
            b = giocatore.getCarteTerritorio().stream().filter(carta -> carta.getId() == tris.get(1)).findAny().get();
            c = giocatore.getCarteTerritorio().stream().filter(carta -> carta.getId() == tris.get(2)).findAny().get();
        } catch (NoSuchElementException e) {
            throw new MossaIllegaleException();
        }

        int standard = truppeStandard(a, b, c);

        // Se standard = 0 il tris non è valido
        if (standard == 0)
            throw new MossaIllegaleException();

        // Rimette le carte usate nel mazzo
        rimettiNelMazzo(mazzo, a, b, c);

        return standard + truppeExtra(a, b, c, giocatore);
    }

    /**
     * Calcola il numero di truppe derivanti dalle figure del tris
     */
    private int truppeStandard(CartaTerritorio a, CartaTerritorio b, CartaTerritorio c) {
        // tris di cannoni
        if (figureUguali(a, b, c) && a.getFigura() == CartaTerritorio.Figura.CANNONE)
            return 4;

        // tris di fanti
        if (figureUguali(a, b, c) && a.getFigura() == CartaTerritorio.Figura.FANTE)
            return 6;

        // tris di cavalli
        if (figureUguali(a, b, c) && a.getFigura() == CartaTerritorio.Figura.CAVALLO)
            return 8;

        // cannone - fante - cavallo
        if (!coppiaUguale(a, b, c) && !figureUguali(a, b, c) && !jollyInTris(a, b, c))
            return 10;

        // jolly più due carte uguali tra loro
        if (coppiaUguale(a, b, c) && jollyInTris(a, b, c))
            return 12;

        // qualsiasi altra combinazione non è un tris valido e ritornerà zero
        return 0;
    }

    /**
     * Se il giocatore possiede nella combinazione carte raffiguranti territori da lui occupati in quel momento,
     * riceve 2 armate in più per ciascuna di queste carte.
     */
    private int truppeExtra(CartaTerritorio a, CartaTerritorio b, CartaTerritorio c, Giocatore giocatore) {
        int truppe = 0;

        if (giocatore.getStati().contains(a.getStatoRappresentato()))
            truppe += 2;

        if (giocatore.getStati().contains(b.getStatoRappresentato()))
            truppe += 2;

        if (giocatore.getStati().contains(c.getStatoRappresentato()))
            truppe += 2;

        return truppe;
    }

    /**
     * Controlla se tre carte territorio hanno la stessa figura.
     */
    private boolean figureUguali(CartaTerritorio a, CartaTerritorio b, CartaTerritorio c) {
        return a.getFigura() == b.getFigura() && b.getFigura() == c.getFigura();
    }

    /**
     * Controlla se due carte hanno la stessa figura e la terza ha una figura diversa. Le due carte uguali non devono
     * essere dei jolly.
     */
    private boolean coppiaUguale(CartaTerritorio a, CartaTerritorio b, CartaTerritorio c) {
        if (a.getFigura() == b.getFigura() && b.getFigura() != c.getFigura() && b.getFigura() != CartaTerritorio.Figura.JOLLY)
            return true;

        if (a.getFigura() == c.getFigura() && c.getFigura() != b.getFigura() && c.getFigura() != CartaTerritorio.Figura.JOLLY)
            return true;

        if (b.getFigura() == c.getFigura() && c.getFigura() != a.getFigura() && c.getFigura() != CartaTerritorio.Figura.JOLLY)
            return true;

        return false;
    }

    /**
     * Controlla se tra le carte territorio è presente un jolly.
     */
    private boolean jollyInTris(CartaTerritorio a, CartaTerritorio b, CartaTerritorio c) {
        if (a.getFigura() == CartaTerritorio.Figura.JOLLY)
            return true;

        if (b.getFigura() == CartaTerritorio.Figura.JOLLY)
            return true;

        if (c.getFigura() == CartaTerritorio.Figura.JOLLY)
            return true;

        return false;
    }

    private void rimettiNelMazzo(List<CartaTerritorio> mazzo, CartaTerritorio a, CartaTerritorio b, CartaTerritorio c) {
        // Reinserisce le carte in fondo al mazzo
        mazzo.add(a);
        mazzo.add(b);
        mazzo.add(c);
    }
}
