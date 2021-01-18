package it.engsoft.risiko.model;

import it.engsoft.risiko.exceptions.ModelDataException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    public Mappa(String nome, String descrizione, int numMinGiocatori, int numMaxGiocatori) {
        this.nome = nome;
        this.descrizione = descrizione;
        this.numMinGiocatori = numMinGiocatori;
        this.numMaxGiocatori = numMaxGiocatori;
        this.continenti = new ArrayList<>();
    }

    public Mappa() {}

    public Long getId() {
        return id;
    }

    // nome
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty())
            throw new ModelDataException("Nome mappa in Mappa.setNome nullo o mancante");
        this.nome = nome;
    }

    // descrizione
    public String getDescrizione() {
        return descrizione;
    }

    /**
     * Imposta la descrizione della mappa.
     *
     * @param descrizione una breve descrizione testuale della mappa
     */
    public void setDescrizione(String descrizione) {
        if (descrizione == null)
            throw new ModelDataException("Descrizione mappa in Mappa.setDescrizione nulla");
        this.descrizione = descrizione;
    }

    // numero minimo giocatori
    public int getNumMinGiocatori() {
        return numMinGiocatori;
    }

    public void setNumMinGiocatori(int numMinGiocatori) {
        if (numMinGiocatori < 2)
            throw new ModelDataException("Numero giocatori minimo in Mappa.setNumMinGiocatori inferiore a 2");
        this.numMinGiocatori = numMinGiocatori;
    }

    // numero massimo giocatore
    public int getNumMaxGiocatori() {
        return numMaxGiocatori;
    }

    public void setNumMaxGiocatori(int numMaxGiocatori) {
        if (numMaxGiocatori > 8)
            throw new ModelDataException("Numero giocatori massimo in Mappa.setNumMaxGiocatori superiore a 8");
        this.numMaxGiocatori = numMaxGiocatori;
    }

    // continenti
    public List<Continente> getContinenti() {
        return continenti;
    }

    public void setContinenti(List<Continente> continenti) {
        if (continenti == null)
            throw new ModelDataException("Continenti appartenenti alla mappa in Mappa.setContinenti nulli");
        this.continenti = continenti;
    }

    public void aggiungiContinente(Continente continente) {
        continenti.add(continente);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mappa mappa = (Mappa) o;
        return Objects.equals(id, mappa.id);
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

}
