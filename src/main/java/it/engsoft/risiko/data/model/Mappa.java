package it.engsoft.risiko.data.model;

import it.engsoft.risiko.exceptions.NotFoundException;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Rappresenta una mappa del Risiko.
 */
@Entity(name = "mappe")
public class Mappa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String descrizione;
    private int numMinGiocatori;
    private int numMaxGiocatori;
    @OneToMany(mappedBy = "mappa", cascade = CascadeType.PERSIST)
    private List<Continente> continenti;

    /**
     * Crea la mappa fornendone tutti gli attributi essenziali.
     * @param nome il nome della mappa
     * @param descrizione la descrizione della mappa
     * @param numMinGiocatori il numero minimo di giocatori con cui si può giocare con questa mappa
     * @param numMaxGiocatori il numero massimo di giocatori con cui si può giocare con questa mappa
     * @param continenti i continenti di questa mappa
     */
    public Mappa(String nome, String descrizione, int numMinGiocatori, int numMaxGiocatori, List<Continente> continenti) {
        this.nome = nome;
        this.descrizione = descrizione;
        this.numMinGiocatori = numMinGiocatori;
        this.numMaxGiocatori = numMaxGiocatori;
        this.continenti = continenti;
        this.continenti.forEach(c -> c.setMappa(this));
    }

    /**
     * Costruttore vuoto. Usato da Hibernate per salvare nuovi record.
     */
    public Mappa() {}

    /**
     * Ritorna l'id della mappa.
     * @return l'id della mappa
     */
    public Long getId() {
        return id;
    }

    /**
     * Ritorna il nome della mappa.
     * @return il nome della mappa
     */
    public String getNome() {
        return nome;
    }

    /**
     * Ritorna la descrizione della mappa.
     * @return la descrizione della mappa
     */
    public String getDescrizione() {
        return descrizione;
    }

    /**
     * Ritorna il numero minimo di giocatori che possono giocare con questa mappa.
     * @return il numero minimo di giocatori che possono giocare con questa mappa
     */
    public int getNumMinGiocatori() {
        return numMinGiocatori;
    }

    /**
     * Ritorna il numero massimo di giocatori che possono giocare con questa mappa.
     * @return il numero massimo di giocatori che possono giocare con questa mappa
     */
    public int getNumMaxGiocatori() {
        return numMaxGiocatori;
    }

    /**
     * Ritorna l'elenco dei continenti di cui questa mappa è composta.
     * @return l'elenco dei continenti
     */
    public List<Continente> getContinenti() {
        return Collections.unmodifiableList(continenti);
    }

    /**
     * Compatta la mappa accorpando stati tra loro vicini e secondo la modalità specificata. Se la modalità è completa,
     * nessun accorpamento viene effettuato; se è ridotta, la mappa risultate ha circa 2/3 degli stati di partenza;
     * se è veloce, la mappa risultante ha circa 1/2 degli stati di partenza.
     * @param modalita la modalità di accorpamento
     */
    public void compatta(Modalita modalita) {
        if (modalita.equals(Modalita.COMPLETA))
            return;

        for (Continente continente: continenti) {
            int numStatiDaRimuovere;
            if (modalita.equals(Modalita.RIDOTTA))
                numStatiDaRimuovere = continente.getStati().size() / 3;
            else
                numStatiDaRimuovere = continente.getStati().size() / 2;

            for (int i = 0; i < numStatiDaRimuovere; i++) {
                Stato daRimuovere = continente.getStati().get(0);
                Stato statoCompattato = daRimuovere.getConfinanti().get(0);
                statoCompattato.trasferisciConfini(daRimuovere);
                continente.getStati().remove(daRimuovere);
            }

        }
    }

    /**
     * Ritorna un elenco di tutti gli stati compresi nella mappa.
     * @return la lista degli stati
     */
    public List<Stato> getStati() {
        return continenti.stream()
                .flatMap(continente -> continente.getStati().stream())
                .collect(Collectors.toList());
    }

    /**
     * Ritorna lo stato della mappa corrispondente all'id passato come parametro.
     * @param id l'id dello stato da cercare
     * @return lo stato cercato
     */
    public Stato getStatoById(Long id) {
        Optional<Stato> stato = getStati().stream().filter(s -> s.getId().equals(id)).findFirst();
        if (stato.isEmpty())
            throw new NotFoundException("Stato non trovato");
        return stato.get();
    }

    /**
     * Confronta questo oggetto con uno fornito come parametro e ritorna true se questi rappresentano la stessa mappa.
     * @param o l'oggetto da confrontare
     * @return true se questo oggetto e quello fornito rappresentano la stessa mappa
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mappa mappa = (Mappa) o;
        return Objects.equals(id, mappa.id);
    }

    /**
     * Ritorna l'hashcode della mappa.
     * @return l'hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
