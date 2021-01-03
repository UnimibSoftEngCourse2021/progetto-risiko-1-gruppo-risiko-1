package it.engsoft.risiko.service;

import it.engsoft.risiko.model.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CarteObiettivoService {
    private final Random random;

    public CarteObiettivoService() {
        this.random = new Random();
    }

    /**
     * Imposta l'obiettivo dei giocatori nella lista fornita.
     * Per farlo viene prima creata una lista di obiettivi di vario genere, tra i quali vengono estratti quelli da
     * assegnare ai giocatori.
     * @param mappa la mappa dove si sta svolgendo la partita
     * @param giocatori l'elenco dei giocatori
     */
    public void setObiettiviGiocatori(Mappa mappa, List<Giocatore> giocatori) {
        // crea gli obiettivi di vario tipo
        ConqTerritori conqTerritori = generaObiettivoConquistaTerritori(mappa);
        ConqTerritori conqTerritoriAlternativo = generaObiettivoConqTerritoriAlternativo(mappa);
        List<ConqGiocatore> congGiocatori = generaObiettivoConquistaGiocatori(giocatori, conqTerritori);

        /* il numero di conqContinenti deve essere massimo pari al numero di giocatori, ma può essere di meno se
            non ci sono abbastanza coppie di continenti */
        int numContinenti = mappa.getContinenti().size();
        int maxCoppie = numContinenti * numContinenti - numContinenti;
        int numObiettiviConqContinenti = Math.min(maxCoppie, giocatori.size());
        List<ConqContinenti> conqContinenti = generaObiettivoConquistaContinenti(mappa, numObiettiviConqContinenti);

        // aggrega tutti gli obiettivi in un'unica lista
        List<Obiettivo> obiettivi = Arrays.asList(conqTerritori, conqTerritoriAlternativo);
        obiettivi.addAll(congGiocatori);
        obiettivi.addAll(conqContinenti);

        // disordina la lista in modo random
        Collections.shuffle(obiettivi);

        // prendi i primi obiettivi dalla lista random e assegnali ai giocatori
        for (int i = 0; i < giocatori.size(); i++) {
            giocatori.get(i).setObiettivo(obiettivi.get(i));
        }
    }

    private ConqTerritori generaObiettivoConquistaTerritori(Mappa mappa) {
        int target = mappa.getStati().size() * 24 / 42; // mantiene la stessa proporzione della mappa reale del Risiko
        return new ConqTerritori(target, 1);
    }

    private ConqTerritori generaObiettivoConqTerritoriAlternativo(Mappa mappa) {
        int target = mappa.getStati().size() * 18 / 42; // mantiene la stessa proporzione della mappa reale del Risiko
        return new ConqTerritori(target, 2);
    }

    private List<ConqGiocatore> generaObiettivoConquistaGiocatori(List<Giocatore> giocatori, ConqTerritori fallback) {
        return giocatori.stream()
                .map(giocatore -> new ConqGiocatore(giocatore, fallback))
                .collect(Collectors.toList());
    }

    private List<ConqContinenti> generaObiettivoConquistaContinenti(Mappa mappa, int quantita) {
        int totaleStati = mappa.getStati().size();
        double soglia = 0.3; // se i due continenti sono meno del 30% della mappa, ne viene aggiunto uno a scelta

        List<ConqContinenti> candidati = new ArrayList<>();

        for (Continente primoContinente : mappa.getContinenti()) {
            for (Continente secondoContinente: mappa.getContinenti()) {
                if (!primoContinente.equals(secondoContinente)) {
                    int statiContinenti = primoContinente.getStati().size() + secondoContinente.getStati().size();
                    int extra = statiContinenti < totaleStati * soglia ? 1 : 0;
                    candidati.add(new ConqContinenti(Arrays.asList(primoContinente, secondoContinente), extra));
                }
            }
        }

        Collections.shuffle(candidati);
        return candidati.subList(0, quantita);
    }
}
