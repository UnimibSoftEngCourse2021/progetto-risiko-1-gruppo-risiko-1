package it.engsoft.risiko.model;

import it.engsoft.risiko.exceptions.ModelDataException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity(name = "continenti")
public class Continente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private int armateBonus;
    @ManyToOne
    @JoinColumn(name = "mappa_id", nullable = false)
    private Mappa mappa;
    @OneToMany(mappedBy = "continente", cascade = CascadeType.PERSIST)
    private List<Stato> stati;

    protected Continente(String nome, int armateBonus) {
        this.nome = nome;
        this.armateBonus = armateBonus;
        this.stati = new ArrayList<>();
    }

    public Continente() {}

    public Long getId() {
        return id;
    }

    // nome
    public String getNome() {
        return nome;
    }

    // armate bonus
    public int getArmateBonus() {
        return armateBonus;
    }

    public Mappa getMappa() {
        return mappa;
    }

    protected void setMappa(Mappa mappa) {
        if (mappa == null)
            throw new ModelDataException("Mappa in Continente.setMappa nulla");

        this.mappa = mappa;
    }

    // stati
    public List<Stato> getStati() {
        return stati;
    }

    // il metodo ritorna il giocatore che possiede tutti gli stati che compongono un continente.
    // se il continente non appartiene ad un solo giocatore ritorna null.
    public Giocatore getProprietario() {
        for (int i = 0; i < getStati().size() - 1; i++) {
            if (!getStati().get(i).getProprietario().equals(getStati().get(i + 1).getProprietario()))
                return null;
        }
        return getStati().get(0).getProprietario();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Continente that = (Continente) o;
        return Objects.equals(id, that.id);
    }
}
