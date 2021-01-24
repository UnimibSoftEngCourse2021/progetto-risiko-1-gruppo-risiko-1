package it.engsoft.risiko.rest.dto;

import it.engsoft.risiko.data.model.Continente;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe utilizzata per gestire i dati in uscita relativi ai continenti.
 */
public final class ContinenteDTO {
    private final Long id;
    private final String nome;
    private final int armateBonus;
    private final List<StatoDTO> stati;

    /**
     * Crea un oggetto contenente i dati relativi ad un continente.
     * @param continente Il continente da cui prendere i dati
     */
    public ContinenteDTO(Continente continente) {
        this.id = continente.getId();
        this.nome = continente.getNome();
        this.armateBonus = continente.getArmateBonus();
        this.stati = continente.getStati()
                .stream()
                .map(StatoDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Restituisce l'id del continente.
     * @return l'id del continente
     */
    public Long getId() {
        return id;
    }

    /**
     * Restituisce il nome del continente.
     * @return il nome del continente
     */
    public String getNome() {
        return nome;
    }

    /**
     * Restituisce il numero di armate bonus garantite dal possesso del continente.
     * @return il numero di armate bonus garantite dal possesso del continente
     */
    public int getArmateBonus() {
        return armateBonus;
    }

    /**
     * Restituisce la lista di stati appartenenti al continente.
     * @return lista di stati in formato StatoDTO
     */
    public List<StatoDTO> getStati() {
        return stati;
    }
}
