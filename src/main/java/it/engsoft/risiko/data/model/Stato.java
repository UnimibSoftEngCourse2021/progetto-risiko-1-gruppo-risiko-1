package it.engsoft.risiko.data.model;

import it.engsoft.risiko.exceptions.ModelDataException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity(name = "stati")
public class Stato {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private String nome;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "adiacenza",
            joinColumns = @JoinColumn(name = "stato1"),
            inverseJoinColumns = @JoinColumn(name = "stato2")
    )
    private List<Stato> confinanti;

    @ManyToOne
    @JoinColumn(name = "continente_id", nullable = false)
    private Continente continente;

    @Transient
    private int armate;

    @Transient
    private Giocatore proprietario;

    public Stato(String nome, Continente continente) {
        this.nome = nome;
        this.continente = continente;
        this.confinanti = new ArrayList<>();
    }

    public Stato() {}

    public Long getId() {
        return id;
    }

    // nome
    public String getNome() {
        return nome;
    }

    // armate
    public int getArmate() {
        return armate;
    }

    public void aggiungiArmate(int n) {
        if (n <= 0)
            throw new ModelDataException("Inserito un numero negativo o nullo di armate in Stato.aggiungiArmate");
        armate = armate + n;
    }

    public void rimuoviArmate(int n) {
        // può essere che le armate di uno stato siano 0: oltre alla fase iniziale, ciò può succedere anche brevemente
        // tra una combattimento con conquista e lo spostamento che ne consegue

        if (armate - n < 0)
            throw new ModelDataException("Inserito un numero negativo di armate in Stato.rimuoviArmate");

        armate = armate - n;
    }

    //stati confinanti
    public List<Stato> getConfinanti() {
        return confinanti;
    }

    public Continente getContinente() {
        return continente;
    }

    // giocatore proprietario dello stato
    public Giocatore getProprietario() {
        return proprietario;
    }

    public void setProprietario(Giocatore proprietario) {
        if (proprietario == null)
            throw new ModelDataException("Giocatore prorprietario dello stato in Stato.setProprietario nullo");
        this.proprietario = proprietario;
    }

    // ritorna vero se lo stato è confinante, falso altimenti
    public boolean isConfinante(Stato stato) {
        return confinanti.contains(stato);
    }

    public void merge(Stato stato) {
        confinanti.remove(stato);
        stato.confinanti.remove(this);
        stato.confinanti.forEach(conf -> {
            if (!this.confinanti.contains(conf)) {
                this.confinanti.add(conf);
                conf.confinanti.add(this);
            }
            conf.confinanti.remove(stato);
        });
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stato stato = (Stato) o;
        return id.equals(stato.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
