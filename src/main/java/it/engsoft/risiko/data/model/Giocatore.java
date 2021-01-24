package it.engsoft.risiko.data.model;

import it.engsoft.risiko.exceptions.ModelDataException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Rappresenta un giocatore nel gioco del Risiko.
 */
public class Giocatore {
    private final List<Stato> stati = new ArrayList<>();
    private String nome;
    private final List<CartaTerritorio> carteTerritorio = new ArrayList<>();
    private Obiettivo obiettivo;
    private Giocatore uccisore;
    private int armateDisponibili = 0;

    /**
     * Crea un giocatore specificandone il nome.
     * @param nome il nome del giocatore
     */
    public Giocatore(String nome){
        this.setNome(nome);
    }

    /**
     * Ritorna il nome del giocatore.
     * @return il nome del giocatore
     */
    public String getNome() {
        return nome;
    }

    private void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty())
            throw new ModelDataException("Nome in Giocatore.setNome non valido");

        this.nome = nome;
    }

    /**
     * Ritorna la lista degli stati occupati dalle armate del giocatore.
     * @return l'elenco di stati conquistati
     */
    public List<Stato> getStati() {
        return stati;
    }

    /**
     * Aggiunge uno stato a quelli conquistati dal giocatore.
     * @param stato il nuovo stato conquistato
     */
    public void aggiungiStato(Stato stato) {
        if (stato == null)
            throw new ModelDataException("Stato in Giocatore.aggiungiStato non valido");
        if(stati.contains(stato))
            throw new ModelDataException("Si è cercato di aggiungere al giocatore " + nome + " uno stato già presente");

        stati.add(stato);
    }

    /**
     * Rimuove uno stato da quelli controllati dal giocatore.
     * @param stato lo stato da rimuovere
     */
    public void rimuoviStato(Stato stato) {
        if(stato == null || !stati.contains(stato))
            throw new ModelDataException("Stato in Giocatore.rimuoviStato non valido");

        stati.remove(stato);
    }

    /**
     * Ritorna la lista di carte territorio di cui il giocatore è in possesso.
     * @return la lista di carte territorio
     */
    public List<CartaTerritorio> getCarteTerritorio() {
        return carteTerritorio;
    }

    /**
     * Aggiungi una carta territorio a quelle del giocatore.
     * @param cartaTerritorio la carta da aggiungere
     */
    public void aggiungiCartaTerritorio(CartaTerritorio cartaTerritorio) {
        if (cartaTerritorio == null)
            throw new ModelDataException("Carta territorio in Giocatore.aggiungiCartaTerritorio nulla");

        carteTerritorio.add(cartaTerritorio);
    }

    /**
     * Rimuove una carta territorio da quelle di cui il giocatore è in possesso.
     * @param cartaTerritorio la carta da rimuovere
     */
    public void rimuoviCartaTerritorio(CartaTerritorio cartaTerritorio) {
        if(cartaTerritorio != null)
            carteTerritorio.remove(cartaTerritorio);
    }

    /**
     * Ritorna l'obiettivo che il giocatore deve raggiungere.
     * @return l'obiettivo da raggiungere
     */
    public Obiettivo getObiettivo() {
        return obiettivo;
    }

    /**
     * Imposta l'obiettivo che il giocatore deve raggiungere.
     * @param obiettivo l'obiettivo del giocatore
     */
    public void setObiettivo(Obiettivo obiettivo) {
        if (obiettivo == null)
            throw new ModelDataException("Obiettivo in Giocatore.setObiettivo nullo");

        if (this.obiettivo != null)
            throw new ModelDataException("L'obiettivo non può essere cambiato");

        this.obiettivo = obiettivo;
    }

    /**
     * Se questo giocatore è stato eliminato, ritorna il giocatore che lo ha sconfitto (ossia che ne ha conquistato
     * l'ultimo territorio); ritorna null altrimenti
     * @return il giocatore che lo ha sconfitto se esiste, null altrimenti
     */
    public Giocatore getUccisore() {
        return uccisore;
    }

    /**
     * Imposta l'uccisore di questo giocatore (ossia colui che ne ha conquistato l'ultimo territorio)
     * @param uccisore colui che ha conquistato l'ultimo territorio di questo giocatore
     */
    public void setUccisore(Giocatore uccisore) {
        if (uccisore == null)
            throw new ModelDataException("Uccisore è null");
        if (equals(uccisore))
            throw new ModelDataException("Un giocatore non può sconfiggere se stesso");
        if (!stati.isEmpty())
            throw new ModelDataException("Il giocatore su cui è chiamato setUccisore non è ancora eliminato");

        this.uccisore = uccisore;
    }

    /**
     * Ritorna il numero di armate disponibili al giocatore per essere posizionate sulla mappa di gioco.
     * @return il numero di armate disponibili
     */
    public int getArmateDisponibili() {
        return armateDisponibili;
    }

    /**
     * Imposta il numero di armate disponibili al giocatore per essere posizionate sulla mappa di gioco.
     * @param armateDisponibili il numero di armate disponibili
     */
    public void setArmateDisponibili(int armateDisponibili) {
        if(armateDisponibili < 0)
            throw new ModelDataException("Truppe disponibili in Giocatore.setTruppeDisponibili è negativo");
        this.armateDisponibili = armateDisponibili;
    }

    /**
     * Modifica il numero di armate disponibili (per il posizionamento) di questo giocatore, aggiungendo il numero
     * di armate fornito (o sottraendo, se negativo).
     * @param armateDisponibili il numero di armate da aggiungere/rimuovere a quelle disponibili
     */
    public void modificaTruppeDisponibili(int armateDisponibili) {
        if(this.armateDisponibili + armateDisponibili < 0)
            throw new ModelDataException("Truppe disponibili in Giocatore.modificaTruppeDisponibili è negativo");
        this.armateDisponibili = this.armateDisponibili + armateDisponibili;
    }

    /**
     * Ritorna true se questo giocatore è stato eliminato.
     * @return true se questo giocatore è stato eliminato
     */
    public boolean isEliminato() { return stati.isEmpty(); }

    /**
     * Ritorna true se questo giocatore ha raggiunto il suo obiettivo, vincendo la partita.
     * @return true se questo giocatore ha raggiunto il suo obiettivo
     */
    public boolean obRaggiunto() {
        return obiettivo.raggiunto(this);
    }

    /**
     * Consegna tutte le carte territorio di questo giocatore al giocatore fornito come parametro, in seguito alla
     * sconfitta.
     * @param giocatore il giocatore a cui consegnare le carte territorio
     */
    public void consegnaCarteTerritorio(Giocatore giocatore) {
        if (giocatore == null)
            throw new ModelDataException("Giocatore null");

        giocatore.getCarteTerritorio().addAll(carteTerritorio);
        carteTerritorio.clear();
    }

    /**
     * Confronta questo oggetto con un altro e ritorna true se rappresentano lo stesso giocatore.
     * @param o l'oggetto da confrontare
     * @return true se rappresentano lo stesso giocatore
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Giocatore giocatore = (Giocatore) o;
        return nome.equals(giocatore.nome);
    }

    /**
     * Ritorna l'hashcode del giocatore.
     * @return l'hashcode del giocatore
     */
    @Override
    public int hashCode() {
        return Objects.hash(nome);
    }
}
