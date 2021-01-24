package it.engsoft.risiko.rest.dto;

import it.engsoft.risiko.exceptions.DatiErratiException;

import java.util.List;

/**
 * Classe contenente i dati inviati dall'utente relativi ad un nuovo stato.
 */
public final class NuovoStatoDTO {
    private final String nome;
    private final List<String> confinanti;

    /**
     * Crea un oggetto contenente i dati relativi ad un nuovo stato.
     * @param nome il nome dello stato
     * @param confinanti la lista con i nomi dei confinanti
     */
    public NuovoStatoDTO(String nome, List<String> confinanti) {
        if(nome == null || nome.trim().isEmpty())
            throw new DatiErratiException("Dati errati: nome stato nullo o mancante");
        this.nome = nome;

        if(confinanti == null || confinanti.isEmpty())
            throw new DatiErratiException("Dati errati: lo stato non ha confinanti");
        this.confinanti = confinanti;
    }

    /**
     * Restituisce il nome dello stato
     * @return nome dello stato
     */
    public String getNome() {
        return nome;
    }

    /**
     * Restituisce la lista contenente i nomi dei confinanti.
     * @return la lista contenente i nomi dei confinanti
     */
    public List<String> getConfinanti() {
        return confinanti;
    }
}
