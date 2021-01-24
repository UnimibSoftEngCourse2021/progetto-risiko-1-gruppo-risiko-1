package it.engsoft.risiko.data.model;

import it.engsoft.risiko.exceptions.ModelDataException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Rappresenta un continente di una specifica mappa del gioco del Risiko.
 */
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

    /**
     * Crea un nuovo continente.
     * @param nome il nome del nuovo continente
     * @param armateBonus il numero di armate bonus che il nuovo continente fornisce
     */
    public Continente(String nome, int armateBonus) {
        this.nome = nome;
        this.armateBonus = armateBonus;
        this.stati = new ArrayList<>();
    }

    /**
     * Costruttore vuoto. Viene usato da Hibernate per la creazione di nuovi record.
     */
    public Continente() {}

    /**
     * Ritorna l'id del continente.
     * @return l'id del continente
     */
    public Long getId() {
        return id;
    }

    /**
     * Ritorna il nome del continente.
     * @return il nome
     */
    public String getNome() {
        return nome;
    }

    /**
     * Ritorna il numero di armate bonus fornite dal continente se conquistato.
     * @return il numero di armate bonus
     */
    public int getArmateBonus() {
        return armateBonus;
    }

    /**
     * Ritorna la mappa di cui questo continente fa parte.
     * @return la mappa di cui questo continente fa parte
     */
    public Mappa getMappa() {
        return mappa;
    }

    /**
     * Imposta la mappa di cui questo continente fa parte.
     * @param mappa la mappa di cui questo continente fa parte.
     */
    protected void setMappa(Mappa mappa) {
        if (mappa == null)
            throw new ModelDataException("Mappa in Continente.setMappa nulla");

        this.mappa = mappa;
    }

    /**
     * Ritorna l'elenco di stati contenuti nel continente.
     * @return l'elenco degli stati
     */
    public List<Stato> getStati() {
        return stati;
    }

    /**
     * Ritorna il giocatore che è proprietario del continente (ossia che ne ha conquistato tutti gli stati) se esiste,
     * null altrimenti.
     * @return il giocatore proprietario se esiste, null altrimenti
     */
    public Giocatore getProprietario() {
        for (int i = 0; i < getStati().size() - 1; i++) {
            if (!getStati().get(i).getProprietario().equals(getStati().get(i + 1).getProprietario()))
                return null;
        }
        return getStati().get(0).getProprietario();
    }

    /**
     * Ritorna true se l'oggetto passato per il confronto rappresenta il medesimo continente di quest'istanza.
     * @param o l'oggetto da confrontare
     * @return true se l'oggetto passato per il confronto rappresenta il medesimo continente di quest'istanza
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Continente that = (Continente) o;
        return Objects.equals(id, that.id);
    }

    /**
     * Ritorna l'hashcode del continente.
     * @return l'hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
