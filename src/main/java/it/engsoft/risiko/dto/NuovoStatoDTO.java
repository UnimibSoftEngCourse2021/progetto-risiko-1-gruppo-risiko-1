package it.engsoft.risiko.dto;

import java.util.List;

public class NuovoStatoDTO {
    private String nome;
    private List<String> confinanti;

    public NuovoStatoDTO(String nome, List<String> confinanti) {
        this.nome = nome;
        this.confinanti = confinanti;
    }

    public String getNome() {
        return nome;
    }

    public List<String> getConfinanti() {
        return confinanti;
    }
}
