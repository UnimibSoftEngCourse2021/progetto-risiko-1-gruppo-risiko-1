package it.engsoft.risiko.data.creators;

import it.engsoft.risiko.data.model.*;
import it.engsoft.risiko.exceptions.ModelDataException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Questa classe factory genera un set di obiettivi validi rispetto alla configurazione (mappa + lista giocatori)
 * specificata in fase di creazione. Tali obiettivi vengono tutti istanziati già nel costruttore, e vengono dopodiché
 * ritornati uno per uno in ordine casuale. Gli obiettivi sono fra loro diversi e nessuno viene mai generato due volte,
 * a meno che non si specifichi unicoObiettivo=true nel costruttore, in tal caso tutti gli obiettivi saranno di tipo
 * ConqTerritori ed uguali fra loro.
 * Dato un numero di giocatori N, gli obiettivi sono generati con le seguenti proporzioni: N obiettivi ConqGiocatore,
 * N obiettivi ConqContinente, 1 obiettivo ConqTerritori "classico", 1 obiettiv ConqTerritori "alternativo".
 */
public class ObiettivoFactory {
    // Vengono mantenute le stesse proporzioni del gioco classico
    private static final float PROPORZIONE_CONQ_TERRITORI_CLASSICO = 24F / 42F;
    private static final float PROPORZIONE_CONQ_TERRITORI_ALTERNATIVO = 18F / 42F;
    // se i due continenti di un obiettivo ConqContinenti occupano meno di questa proporzione della mappa, ne viene
    // aggiunto un terzo a scelta
    private static final float SOGLIA_TERZO_CONTINENTE = 0.3F; // 30 %

    private final List<Obiettivo> obiettivi;

    /**
     * Crea un'istanza di ObiettivoFactory in grado di generare obiettivi validi per la mappa e la lista di giocatori
     * forniti.
     * @param mappa la mappa di gioco rispetto alla quale generare gli obiettivi
     * @param giocatori la lista di giocatori rispetto alla quale generare gli obiettivi
     * @param unicoObiettivo se true, tutti gli obiettivi generati sono fra loro identici e di tipo ConqTerritori
     */
    public ObiettivoFactory(Mappa mappa, List<Giocatore> giocatori, boolean unicoObiettivo) {
        ConqTerritori conquistaTerritori = creaObiettivoConquistaTerritori(mappa);

        if (unicoObiettivo) {
            // uno per ogni giocatore, anche se sono tutti uguali
            obiettivi = new ArrayList<>();
            giocatori.forEach(g -> obiettivi.add(conquistaTerritori));
        } else {
            ConqTerritori conqTerritoriAlternativo = creaObiettivoConqTerritoriAlternativo(mappa);
            List<ConqGiocatore> conqGiocatori = creaObiettivoConquistaGiocatori(giocatori, conquistaTerritori);

        /* il numero di conqContinenti deve essere massimo pari al numero di giocatori, ma può essere di meno se
            non ci sono abbastanza coppie di continenti */
            int numContinenti = mappa.getContinenti().size();
            int maxCoppie = numContinenti * numContinenti - numContinenti;
            int numObiettiviConqContinenti = Math.min(maxCoppie, giocatori.size());
            List<ConqContinenti> conqContinenti = creaObiettivoConquistaContinenti(mappa, numObiettiviConqContinenti);

            // aggrega tutti gli obiettivi in un'unica lista
            obiettivi = new ArrayList<>(Arrays.asList(conquistaTerritori, conqTerritoriAlternativo));
            obiettivi.addAll(conqGiocatori);
            obiettivi.addAll(conqContinenti);

            // disordina la lista in modo random
            Collections.shuffle(obiettivi);
        }

    }

    /**
     * Ritorna un obiettivo valido che non sia già stato ritornato in precedenza da questa istanza. Se non presenta,
     * genera un errore.
     * @return un obiettivo valido
     */
    public Obiettivo getNuovoObiettivo() {
        if (obiettivi.size() == 0)
            throw new ModelDataException("Obiettivi terminati");
        return obiettivi.remove(0);
    }

    private ConqTerritori creaObiettivoConquistaTerritori(Mappa mappa) {
        int target = Math.round(mappa.getStati().size() * PROPORZIONE_CONQ_TERRITORI_CLASSICO);
        return new ConqTerritori(target, 1);
    }

    private ConqTerritori creaObiettivoConqTerritoriAlternativo(Mappa mappa) {
        int target = Math.round(mappa.getStati().size() * PROPORZIONE_CONQ_TERRITORI_ALTERNATIVO);
        return new ConqTerritori(target, 2);
    }

    private List<ConqGiocatore> creaObiettivoConquistaGiocatori(List<Giocatore> giocatori, ConqTerritori fallback) {
        return giocatori.stream()
                .map(giocatore -> new ConqGiocatore(giocatore, fallback))
                .collect(Collectors.toList());
    }

    private List<ConqContinenti> creaObiettivoConquistaContinenti(Mappa mappa, int quantita) {
        int totaleStati = mappa.getStati().size();

        List<ConqContinenti> candidati = new ArrayList<>();

        for (Continente primoContinente : mappa.getContinenti()) {
            for (Continente secondoContinente: mappa.getContinenti()) {
                if (!primoContinente.equals(secondoContinente)) {
                    int statiContinenti = primoContinente.getStati().size() + secondoContinente.getStati().size();
                    int extra = statiContinenti < (totaleStati * SOGLIA_TERZO_CONTINENTE) ? 1 : 0;
                    candidati.add(new ConqContinenti(Arrays.asList(primoContinente, secondoContinente), extra, mappa));
                }
            }
        }

        Collections.shuffle(candidati);
        return candidati.subList(0, quantita);
    }
}
