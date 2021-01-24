package it.engsoft.risiko.rest.dto;

import it.engsoft.risiko.data.model.Stato;

import java.util.List;
import java.util.stream.Collectors;

public final class StatoDTO {
    private final Long id;
    private final String nome;
    private final List<Long> confinanti;

    public StatoDTO(Stato stato) {
        this.id = stato.getId();
        this.nome = stato.getNome();
        this.confinanti = stato.getConfinanti().stream()
                .map(Stato::getId)
                .collect(Collectors.toList());
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public List<Long> getConfinanti() {
        return confinanti;
    }
}
