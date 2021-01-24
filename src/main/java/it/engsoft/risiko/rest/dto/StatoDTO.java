package it.engsoft.risiko.rest.dto;

import it.engsoft.risiko.data.model.Stato;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe utilizzata per gestire i dati in uscita relativi agli stati.
 */
public final class StatoDTO {
    private final Long id;
    private final String nome;
    private final List<Long> confinanti;

    /**
     * Crea un oggetto contenente i dati relativi ad un continente.
     * @param stato lo stato da cui prendere i dati
     */
    public StatoDTO(Stato stato) {
        this.id = stato.getId();
        this.nome = stato.getNome();
        this.confinanti = stato.getConfinanti().stream()
                .map(Stato::getId)
                .collect(Collectors.toList());
    }

    /**
     * Restituisce l'id dello stato.
     * @return l'id dello stato
     */
    public Long getId() {
        return id;
    }

    /**
     * Restituisce il nome dello stato.
     * @return il nome dello stato
     */
    public String getNome() {
        return nome;
    }

    /**
     * Restituisce la lista di id degli stati confinanti.
     * @return lista di id degli stati confinanti
     */
    public List<Long> getConfinanti() {
        return confinanti;
    }
}
