package it.engsoft.risiko.service;

import it.engsoft.risiko.data.model.*;
import it.engsoft.risiko.exceptions.*;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class CarteTerritorioService {
    /**
     * Genera il mazzo e distribuisce le carte ai giocatori.
     */
    public void distribuisciCarte(Partita partita) {
        List<CartaTerritorio> mazzo = partita.getMazzo();

        // Generazione mazzo
        generaMazzo(mazzo, partita.getMappa());

        // mescola il mazzo
        Collections.shuffle(mazzo);

        int g = 0; // indice giocatori

        // La carta 'c' viene assegnata al giocatore 'g'
        for (CartaTerritorio cartaTerritorio : mazzo) {
            if (cartaTerritorio.getFigura() != CartaTerritorio.Figura.JOLLY) {
                // Assegna lo stato rappresentato dalla carta al giocatore
                partita.getGiocatori().get(g).aggiungiStato(cartaTerritorio.getStatoRappresentato());
                cartaTerritorio.getStatoRappresentato().setProprietario(partita.getGiocatori().get(g));

                g = (g + 1) % partita.getGiocatori().size();
            }
        }

        // inverte l'ordine dei giocatori in modo che per primi ci siano quelli con meno territori
        // (ossia più armate da posizionare)
        Collections.reverse(partita.getGiocatori());
    }

    /**
     * Crea le carte e le aggiunge al mazzo.
     *
     * @param mappa: la mappa su cui si svolge la partita
     */
    private void generaMazzo(List<CartaTerritorio> mazzo, Mappa mappa) {
        // numStati viene utilizzato per contare gli stati e come ID per inizializzare le carte
        int idCount = 0;

        for (Stato stato: mappa.getStati()) {
            if (idCount % 3 == 0)
                mazzo.add(new CartaTerritorio(idCount, stato, CartaTerritorio.Figura.CANNONE));
            else if (idCount % 3 == 1)
                mazzo.add(new CartaTerritorio(idCount, stato, CartaTerritorio.Figura.CAVALLO));
            else
                mazzo.add(new CartaTerritorio(idCount, stato, CartaTerritorio.Figura.FANTE));
            idCount++;
        }

        int numJolly = Math.round(idCount / 20F);

        // aggiunge i jolly al mazzo
        for (int i = 0; i < numJolly; i++) {
            CartaTerritorio jolly = new CartaTerritorio(idCount + i, null, CartaTerritorio.Figura.JOLLY);
            mazzo.add(jolly);
        }
    }

    public CartaTerritorio pescaCarta(List<CartaTerritorio> mazzo, Giocatore giocatore) {
        if(mazzo.isEmpty())
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
        // Ottiene le carte corrispondenti agli id
        Optional<CartaTerritorio> optA = giocatore.getCarteTerritorio().stream()
                .filter(carta -> carta.getId() == tris.get(0))
                .findFirst();
        Optional<CartaTerritorio> optB = giocatore.getCarteTerritorio().stream()
                .filter(carta -> carta.getId() == tris.get(1))
                .findFirst();
        Optional<CartaTerritorio> optC = giocatore.getCarteTerritorio().stream()
                .filter(carta -> carta.getId() == tris.get(2))
                .findFirst();

        // Controlla che le carte siano presenti
        if(optA.isEmpty() || optB.isEmpty() || optC.isEmpty())
            throw new MossaIllegaleException("Mossa illegale: le carte non appartengono al giocatore");

        CartaTerritorio a = optA.get();
        CartaTerritorio b = optB.get();
        CartaTerritorio c = optC.get();

        int standard = truppeStandard(a, b, c);

        // Se standard = 0 il tris non è valido
        if (standard == 0)
            throw new MossaIllegaleException("Mossa illegale: tris non valido");

        // Rimette le carte usate nel mazzo
        rimettiNelMazzo(mazzo, a, b, c, giocatore);

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

        return b.getFigura() == c.getFigura() && c.getFigura() != a.getFigura() && c.getFigura() != CartaTerritorio.Figura.JOLLY;
    }

    /**
     * Controlla se tra le carte territorio è presente un jolly.
     */
    private boolean jollyInTris(CartaTerritorio a, CartaTerritorio b, CartaTerritorio c) {
        if (a.getFigura() == CartaTerritorio.Figura.JOLLY)
            return true;

        if (b.getFigura() == CartaTerritorio.Figura.JOLLY)
            return true;

        return c.getFigura() == CartaTerritorio.Figura.JOLLY;
    }

    private void rimettiNelMazzo(List<CartaTerritorio> mazzo, CartaTerritorio a, CartaTerritorio b, CartaTerritorio c, Giocatore giocatore) {
        // Reinserisce le carte in fondo al mazzo
        mazzo.add(a);
        mazzo.add(b);
        mazzo.add(c);

        // Toglie le carte al giocatore
        giocatore.rimuoviCartaTerritorio(a);
        giocatore.rimuoviCartaTerritorio(b);
        giocatore.rimuoviCartaTerritorio(c);
    }
}
