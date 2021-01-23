package it.engsoft.risiko.rest.dao;

import it.engsoft.risiko.data.model.Stato;

import java.util.List;
import java.util.stream.Collectors;

public class StatoDAO {
    private Long id;
    private String nome;
    private List<Long> confinanti;

    public StatoDAO(Stato stato) {
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
