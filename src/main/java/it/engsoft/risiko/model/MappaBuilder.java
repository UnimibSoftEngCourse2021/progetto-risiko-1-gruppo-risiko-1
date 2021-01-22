package it.engsoft.risiko.model;

import it.engsoft.risiko.exceptions.DatiErratiException;

import java.util.*;

public class MappaBuilder {
    private static final int NUM_MIN_GIOCATORI = 2;
    private static final int NUM_MAX_GIOCATORI = 8;
    private static final int MIN_ARMATE_BONUS = 2;
    private static final int MAX_ARMATE_BONUS = 7;
    private static final int MIN_CONTINENTI = 4;
    private static final int MAX_CONTINENTI = 8;
    private static final int MIN_STATI_CONTINENTE = 4;
    private static final int MAX_STATI_CONTINENTE = 12;

    private final String nome;
    private final String descrizione;
    private final int numMinGiocatori;
    private final int numMaxGiocatori;
    private final List<Continente> continenti = new ArrayList<>();
    private final List<Stato> stati = new ArrayList<>();

    public MappaBuilder(String nome, String descrizione, int numMinGiocatori, int numMaxGiocatori) {
        if (nome.trim().isEmpty() || descrizione.trim().isEmpty() || numMinGiocatori < NUM_MIN_GIOCATORI ||
                numMaxGiocatori > NUM_MAX_GIOCATORI)
            throw new DatiErratiException("Parametri mappa non validi");
        this.nome = nome;
        this.descrizione = descrizione;
        this.numMinGiocatori = numMinGiocatori;
        this.numMaxGiocatori = numMaxGiocatori;
    }

    public void addContinente(String nome, int armateBonus) {
        if (nome.trim().isEmpty() || armateBonus < MIN_ARMATE_BONUS || armateBonus > MAX_ARMATE_BONUS)
            throw new DatiErratiException("Dati continente non validi");

        if (continenti.stream().anyMatch(c -> c.getNome().equals(nome)) ||
                stati.stream().anyMatch(s -> s.getNome().equals(nome)))
            throw new DatiErratiException("Nome duplicato");

        continenti.add(new Continente(nome, armateBonus));
    }

    public void addStato(String nome, String nomeContinente) {
        if (stati.stream().anyMatch(s -> s.getNome().equals(nome)) ||
                continenti.stream().anyMatch(c -> c.getNome().equals(nome)))
            throw new DatiErratiException("Nome duplicato");

        Continente cont = continenti.stream().filter(c -> c.getNome().equals(nomeContinente))
                .findFirst()
                .or(() -> {
                    throw new DatiErratiException("Continente non trovato");
                })
                .get();

        Stato s = new Stato(nome, cont);
        stati.add(s);
        cont.getStati().add(s);
    }

    public void addConfine(String nomeStato1, String nomeStato2) {
        Stato stato1 = stati.stream().filter(s -> s.getNome().equals(nomeStato1)).findFirst()
                .or(() -> {
                    throw new DatiErratiException("Stato non trovato");
                }).get();
        Stato stato2 = stati.stream().filter(s -> s.getNome().equals(nomeStato2)).findFirst()
                .or(() -> {
                    throw new DatiErratiException("Stato non trovato");
                }).get();
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
