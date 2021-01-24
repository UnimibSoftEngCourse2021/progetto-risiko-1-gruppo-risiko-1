package it.engsoft.risiko.data.model;

import it.engsoft.risiko.exceptions.ModelDataException;
import it.engsoft.risiko.exceptions.MossaIllegaleException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Rappresenta uno stato (o territorio) sulla mappa del Risiko.
 */
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

    /**
     * Crea un nuovo stato.
     * @param nome il nome del nuovo stato
     * @param continente il continente di cui il nuovo stato fa parte
     */
    public Stato(String nome, Continente continente) {
        this.nome = nome;
        this.continente = continente;
        this.confinanti = new ArrayList<>();
    }

    /**
     * Costruttore vuoto. Viene usato da Hibernate per salvare nuovi record.
     */
    public Stato() {}

    /**
     * Ritorna l'id dello stato.
     * @return l'id dello stato
     */
    public Long getId() {
        return id;
    }

    /**
     * Ritorna il nome dello stato.
     * @return il nome dello stato
     */
    public String getNome() {
        return nome;
    }

    /**
     * Ritorna il numero di armate che attualmente sono posizionate su questo territorio.
     * @return il numero di armate occupanti
     */
    public int getArmate() {
        return armate;
    }

    /**
     * Aggiunge un certo numero di armate occupanti per questo territorio.
     * @param nuoveArmate il numero di armate da aggiungere
     */
    public void aggiungiArmate(int nuoveArmate) {
        if (nuoveArmate <= 0)
            throw new ModelDataException("Inserito un numero negativo o nullo di armate in Stato.aggiungiArmate");
        armate = armate + nuoveArmate;
    }

    /**
     * Rimuove un certo numero di armate occupanti per questo territorio.
     * @param armateDaRimuovere il numero di armate da rimuovere
     */
    public void rimuoviArmate(int armateDaRimuovere) {
        // può essere che le armate di uno stato siano 0: oltre alla fase iniziale, ciò può succedere anche brevemente
        // tra una combattimento con conquista e lo spostamento che ne consegue

        if (armate - armateDaRimuovere < 0)
            throw new ModelDataException("Inserito un numero negativo di armate in Stato.rimuoviArmate");

        armate = armate - armateDaRimuovere;
    }

    /**
     * Ritorna la lista di territori confinanti.
     * @return la lista di territori confinanti
     */
    public List<Stato> getConfinanti() {
        return confinanti;
    }

    /**
     * Ritorna il continente di cui questo territorio fa parte.
     * @return il continente
     */
    public Continente getContinente() {
        return continente;
    }

    /**
     * Ritorna il giocatore proprietario di questo territorio.
     * @return il proprietario
     */
    public Giocatore getProprietario() {
        return proprietario;
    }

    /**
     * Imposta il giocatore proprietario di questo territorio.
     * @param proprietario il nuovo proprietario
     */
    public void setProprietario(Giocatore proprietario) {
        if (proprietario == null)
            throw new ModelDataException("Giocatore prorprietario dello stato in Stato.setProprietario nullo");
        this.proprietario = proprietario;
    }

    /**
     * Ritorna true se lo stato fornito confina con questo territorio.
     * @param stato lo stato di cui verificare se confina
     * @return true se i due stati confinano
     */
    public boolean isConfinante(Stato stato) {
        return confinanti.contains(stato);
    }

    /**
     * Trasferisce tutti gli stati confinanti con quello fornito a questo stato e li rimuove dal suddetto, in
     * preparazione alla rimozione dello stato passato come parametro.
     * @param stato lo stato i cui confini vanno rimossi e passati a questo territorio
     */
    public void trasferisciConfini(Stato stato) {
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

    /**
     * Sposta un certo numero di armate verso uno stato confinante ed appartenente allo stesso giocatore.
     * @param destinazione lo stato destinazione dello spostamento
     * @param quantita il numero di armate da spostare
     */
    public void spostaArmate(Stato destinazione, int quantita) {
        if (!confinanti.contains(destinazione))
            throw new MossaIllegaleException("Non puoi spostare le armate tra stati non confinanti");
        if (!proprietario.equals(destinazione.getProprietario()))
            throw new MossaIllegaleException("Non puoi spostare le armate verso uno stato che non ti appartiene");
        if (quantita < 1 || quantita >= armate)
            throw new MossaIllegaleException("Numero di armate dello spostamento non valide");

        this.rimuoviArmate(quantita);
        destinazione.aggiungiArmate(quantita);
    }

    /**
     * Riceve un oggetto come parametro e ritorna true se esso rappresenta lo stesso stato di quest'istanza.
     * @param o l'oggetto da confrontare
     * @return true se rappresentano lo stesso stato
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stato stato = (Stato) o;
        return id.equals(stato.id);
    }

    /**
     * Ritorna l'hashcode dello stato.
     * @return l'hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
