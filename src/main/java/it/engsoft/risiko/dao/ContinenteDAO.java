package it.engsoft.risiko.dao;

import it.engsoft.risiko.model.Continente;

import java.util.List;
import java.util.stream.Collectors;

public class ContinenteDAO {
    private Long id;
    private String nome;
    private int armateBonus;
    private List<StatoDAO> stati;

    public ContinenteDAO(Continente continente) {
        this.id = continente.getId();
        this.nome = continente.getNome();
        this.armateBonus = continente.getArmateBonus();
        this.stati = continente.getStati()
                .stream()
                .map(StatoDAO::new)
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

    public List<StatoDAO> getStati() {
        return stati;
    }
}
