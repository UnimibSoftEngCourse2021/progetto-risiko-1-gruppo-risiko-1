package it.engsoft.risiko.http.dto;

import it.engsoft.risiko.data.model.Giocatore;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe utilizzata per gestire i dati in uscita relativi ad un giocatore.
 */
public final class GiocatoreDTO {
    private final List<Long> idStati = new ArrayList<>();
    private final String nome;
    private final List<CartaTerritorioDTO> carteTerritorio;
    private final String obiettivo;
    private final String uccisore;
    private final int truppeDisponibili;

    /**
     * Crea un oggetto contenente i dati più rilevanti di un giocatore
     * @param giocatore Il giocatore da cui prendere i dati
     */
    public GiocatoreDTO(Giocatore giocatore) {
        giocatore.getStati().forEach(stato ->
                this.idStati.add(stato.getId()));
        this.nome = giocatore.getNome();
        this.carteTerritorio = giocatore.getCarteTerritorio().stream()
                .map(CartaTerritorioDTO::new)
                .collect(Collectors.toList());
        this.obiettivo = giocatore.getObiettivo().getDescrizione();
        this.uccisore = giocatore.getUccisore() == null ? null : giocatore.getUccisore().getNome();
        this.truppeDisponibili = giocatore.getArmateDisponibili();
    }

    /**
     * Restituisce gli id degli stati appartenenti al giocatore.
     * @return id degli stati appartenenti al giocatore
     */
    public List<Long> getIdStati() {
        return idStati;
    }

    /**
     * Restituisce il nome del giocatore.
     * @return il nome del giocatore
     */
    public String getNome() {
        return nome;
    }

    /**
     * Restituisce la lista di carte territorio appartenenti al giocatore.
     * @return lista di carte territorio in formato CartaTerritorioDTO
     */
    public List<CartaTerritorioDTO> getCarteTerritorio() {
        return carteTerritorio;
    }

    /**
     * Restituisce l'obiettivo del giocatore.
     * @return Obiettivo in formato testo
     */
    public String getObiettivo() {
        return obiettivo;
    }

    /**
     * Restituisce il nome del giocatore che ha eliminato il giocatore rappresentato.
     * @return nome del giocatore che ha eliminato il giocatore rappresentato
     */
    public String getUccisore() {
        return uccisore;
    }

    /**
     * Restituisce il numero di truppe che il giocatore deve piazzare sulla mappa.
     * @return il numero di truppe che il giocatore deve piazzare sulla mappa
     */
    public int getTruppeDisponibili() {
        return truppeDisponibili;
    }
}