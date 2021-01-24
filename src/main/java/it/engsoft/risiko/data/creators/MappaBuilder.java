package it.engsoft.risiko.data.creators;

import it.engsoft.risiko.data.model.Continente;
import it.engsoft.risiko.data.model.Mappa;
import it.engsoft.risiko.data.model.Stato;
import it.engsoft.risiko.exceptions.DatiErratiException;

import java.util.*;

/**
 * Questa classe si occupa della costruzione di un oggetto Mappa, verificando la correttezza di tutti i parametri
 * inseriti.
 */
public class MappaBuilder {
    private static final int NUM_MIN_GIOCATORI = 2; // numero minimo di giocatori per una mappa
    private static final int NUM_MAX_GIOCATORI = 8; // numero max di giocatori per un mappa
    private static final int MIN_ARMATE_BONUS = 2; // num minimo di armate bonus di un continente
    private static final int MAX_ARMATE_BONUS = 7; // num max di armate bonus di un continente
    private static final int MIN_CONTINENTI = 4; // num min di continenti in una mappa
    private static final int MAX_CONTINENTI = 8; // num max di continenti in una mappa
    private static final int MIN_STATI_CONTINENTE = 4; // num min di stati in un continente
    private static final int MAX_STATI_CONTINENTE = 12; // num max di stati in un continente

    private final String nome;
    private final String descrizione;
    private final int numMinGiocatori;
    private final int numMaxGiocatori;
    private final List<Continente> continenti = new ArrayList<>();
    private final List<Stato> stati = new ArrayList<>();

    /**
     * Istanzia un nuovo MappaBuilder ricevendo come parametri le informazioni generali della mappa da creare.
     * @param nome il nome della mappa da creare
     * @param descrizione la descrizione della mappa da creare
     * @param numMinGiocatori il numero minimo di giocatori che la mappa può avere
     * @param numMaxGiocatori il numero massimo di giocatori che la mappa può avere
     */
    public MappaBuilder(String nome, String descrizione, int numMinGiocatori, int numMaxGiocatori) {
        if (nome.trim().isEmpty() || descrizione.trim().isEmpty() || numMinGiocatori < NUM_MIN_GIOCATORI ||
                numMaxGiocatori > NUM_MAX_GIOCATORI)
            throw new DatiErratiException("Parametri mappa non validi");
        this.nome = nome;
        this.descrizione = descrizione;
        this.numMinGiocatori = numMinGiocatori;
        this.numMaxGiocatori = numMaxGiocatori;
    }

    /**
     * Aggiunge un continente alla mappa in fase di creazione.
     * @param nome il nome del continente da aggiungere
     * @param armateBonus il numero di armate bonus che si riceve se si conquista il continente aggiunto
     */
    public void addContinente(String nome, int armateBonus) {
        if (nome.trim().isEmpty() || armateBonus < MIN_ARMATE_BONUS || armateBonus > MAX_ARMATE_BONUS)
            throw new DatiErratiException("Dati continente non validi");

        if (continenti.stream().anyMatch(c -> c.getNome().equals(nome)) ||
                stati.stream().anyMatch(s -> s.getNome().equals(nome)))
            throw new DatiErratiException("Nome duplicato");

        continenti.add(new Continente(nome, armateBonus));
    }

    /**
     * Aggiunge uno stato alla mappa in fase di creazione.
     * @param nome il nome dello stato da aggiungere
     * @param nomeContinente il nome del continente di cui questo nuovo stato fa parte
     */
    public void addStato(String nome, String nomeContinente) {
        if (stati.stream().anyMatch(s -> s.getNome().equals(nome)) ||
                continenti.stream().anyMatch(c -> c.getNome().equals(nome)))
            throw new DatiErratiException("Nome duplicato");

        Optional<Continente> optCont = continenti.stream()
                .filter(c -> c.getNome().equals(nomeContinente))
                .findFirst();

        if(optCont.isEmpty())
            throw new DatiErratiException("Continente non trovato");

        Continente cont = optCont.get();

        Stato s = new Stato(nome, cont);
        stati.add(s);
        cont.getStati().add(s);
    }

    /**
     * Aggiunge un confine tra due stati alla mappa in fase di creazione, se esso non è già presente.
     * @param nomeStato1 il nome del primo stato
     * @param nomeStato2 il nome del secondo stato
     */
    public void addConfine(String nomeStato1, String nomeStato2) {
        Optional<Stato> optStato1 = stati.stream().filter(s -> s.getNome().equals(nomeStato1)).findFirst();
        Optional<Stato> optStato2 = stati.stream().filter(s -> s.getNome().equals(nomeStato2)).findFirst();

        if(optStato1.isEmpty() || optStato2.isEmpty())
            throw new DatiErratiException("Stato non trovato");

        Stato stato1 = optStato1.get();
        Stato stato2 = optStato2.get();

        // inserisci solo se non è già stato inserito
        if (stato1.getConfinanti().stream().noneMatch(conf1 -> conf1.getNome().equals(nomeStato2)) &&
                stato2.getConfinanti().stream().noneMatch(conf2 -> conf2.getNome().equals(nomeStato1))) {
            stato1.getConfinanti().add(stato2);
            stato2.getConfinanti().add(stato1);
        }
    }

    private boolean grafoConnesso() {
        List<Stato> visitati = new ArrayList<>();
        LinkedList<Stato> queue = new LinkedList<>();

        visitati.add(stati.get(0));
        queue.offer(stati.get(0));

        while (!queue.isEmpty()) {
            Stato statoCorrente = queue.poll();

            for (Stato confinante : statoCorrente.getConfinanti()) {
                if (visitati.stream().noneMatch(v -> v.getNome().equals(confinante.getNome()))) {
                    visitati.add(confinante);
                    queue.offer(confinante);
                }
            }
        }
        return (visitati.size() == stati.size());
    }

    /**
     * Costruisce una mappa sfruttando le informazioni (stati, confini, continenti) inserite fino a questo momento, e
     * verificando che la mappa così ottenuta sia valida.
     * @return la mappa costruita
     */
    public Mappa build() {
        if (continenti.size() < MIN_CONTINENTI || continenti.size() > MAX_CONTINENTI)
            throw new DatiErratiException("Numero di continenti non valido");
        if (continenti.stream().anyMatch(c -> c.getStati().size() < MIN_STATI_CONTINENTE ||
                c.getStati().size() > MAX_STATI_CONTINENTE))
            throw new DatiErratiException("Numero di stati nei continenti non valido");
        if (!grafoConnesso())
            throw new DatiErratiException("La mappa non è connessa");

        return new Mappa(nome, descrizione, numMinGiocatori, numMaxGiocatori, continenti);
    }
}
