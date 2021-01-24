package it.engsoft.risiko.rest.dto;

import it.engsoft.risiko.data.model.Continente;

import java.util.List;
import java.util.stream.Collectors;

public final class ContinenteDTO {
    private final Long id;
    private final String nome;
    private final int armateBonus;
    private final List<StatoDTO> stati;

    public ContinenteDTO(Continente continente) {
        this.id = continente.getId();
        this.nome = continente.getNome();
        this.armateBonus = continente.getArmateBonus();
        this.stati = continente.getStati()
                .stream()
                .map(StatoDTO::new)
                .collect(Collectors.toList());
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public int getArmateBonus() {
        return armateBonus;
    }

    public List<StatoDTO> getStati() {
        return stati;
    }
}
