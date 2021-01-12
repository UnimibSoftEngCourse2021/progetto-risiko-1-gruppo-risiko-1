package it.engsoft.risiko.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty())
            throw new RuntimeException("Nome stato nullo o mancante");
        this.nome = nome;
    }

    // TODO: non sarebbe meglio un solo metodo aggiungi armate al quale si possono passare valori negativi?
    // armate
    public int getArmate() {
        return armate;
    }

    public void aggiungiArmate(int n) {
        if (n <= 0)
            throw new RuntimeException("Inserito un numero negativo o nullo di armate");
        armate = armate + n;
    }

    public void rimuoviArmate(int n) {
        if (armate - n < 0)
            throw new RuntimeException("Inserito un numero negativo di armate");
        armate = armate - n;
    }


    //stati confinanti
    public List<Stato> getConfinanti() {
        return confinanti;
    }

    public void aggiungiConfinante(Stato stato) {
        confinanti.add(stato);
    }

    // se uno stato (y) è confinante di un altro (x) dovrebbe valere anche il contrario; con l'attuale setter cio' non avviene
    public void setConfinanti(ArrayList<Stato> confinanti) {
        if (confinanti == null)
            throw new RuntimeException("Stati confinanti nulli");
        this.confinanti = confinanti;
    }

    // giocatore proprietario dello stato
    public Giocatore getProprietario() {
        return proprietario;
    }

    public void setProprietario(Giocatore proprietario) {
        if (proprietario == null)
            throw new RuntimeException("Giocatore prorprietario dello stato nullo");
        this.proprietario = proprietario;
    }

    // ritorna vero se lo stato è confinante, falso altimenti
    public boolean isConfinante(Stato stato) {
        return confinanti.contains(stato);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stato stato = (Stato) o;
        return id.equals(stato.id);
    }

}
