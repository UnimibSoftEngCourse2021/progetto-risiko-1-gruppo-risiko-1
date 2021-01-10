package it.engsoft.risiko.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity(name = "continenti")
public class Continente {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String nome;
    private int armateBonus;
    @ManyToOne
    @JoinColumn(name = "mappa_id", nullable = false)
    private Mappa mappa;
    @OneToMany(mappedBy = "continente")
    private List<Stato> stati;

    public Long getId() {
        return id;
    }

    // nome
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty())
            throw new RuntimeException("Nome continente nullo o mancante");
        this.nome = nome;
    }

    // armate bonus
    public int getArmateBonus() {
        return armateBonus;
    }

    public void setArmateBonus(int armateBonus) {
        if (armateBonus <= 0)
            throw new RuntimeException("Armate bonus zero o negative");
        this.armateBonus = armateBonus;
    }

    // stati
    public List<Stato> getStati() {
        return stati;
    }

    public void setStati(ArrayList<Stato> stati) {
        if (stati == null)
            throw new RuntimeException("Stati apparteneti al continente nulli");
        this.stati = stati;
    }

    public void aggiungiStato(Stato stato) {
        stati.add(stato);
    }

    public void rimuoviStato(Stato stato) {
        boolean ris = stati.remove(stato);
        if (!ris)
            throw new RuntimeException("Stato rimosso non esiste");
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
