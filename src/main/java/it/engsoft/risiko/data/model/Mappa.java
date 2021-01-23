package it.engsoft.risiko.data.model;

import it.engsoft.risiko.exceptions.NotFoundException;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

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

    public Mappa(String nome, String descrizione, int numMinGiocatori, int numMaxGiocatori, List<Continente> continenti) {
        this.nome = nome;
        this.descrizione = descrizione;
        this.numMinGiocatori = numMinGiocatori;
        this.numMaxGiocatori = numMaxGiocatori;
        this.continenti = continenti;
        this.continenti.forEach(c -> c.setMappa(this));
    }

    public Mappa() {}

    public Long getId() {
        return id;
    }

    // nome
    public String getNome() {
        return nome;
    }

    // descrizione
    public String getDescrizione() {
        return descrizione;
    }

    // numero minimo giocatori
    public int getNumMinGiocatori() {
        return numMinGiocatori;
    }

    // numero massimo giocatore
    public int getNumMaxGiocatori() {
        return numMaxGiocatori;
    }

    // continenti
    public List<Continente> getContinenti() {
        return Collections.unmodifiableList(continenti);
    }

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
                statoCompattato.merge(daRimuovere);
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
        return getStati().stream().filter(s -> s.getId().equals(id)).findFirst()
                .or(() -> {
                    throw new NotFoundException("Stato non trovato");
                }).get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mappa mappa = (Mappa) o;
        return Objects.equals(id, mappa.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
