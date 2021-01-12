package it.engsoft.risiko.dto;

import java.util.List;

public class NuovoContinenteDTO {
    private String nome;
    private int armateBonus;
    private List<NuovoStatoDTO> stati;

    public NuovoContinenteDTO(String nome, int armateBonus, List<NuovoStatoDTO> stati) {
        this.nome = nome;
        this.armateBonus = armateBonus;
        this.stati = stati;
    }

    public String getNome() {
        return nome;
    }

    public int getArmateBonus() {
        return armateBonus;
    }

    public List<NuovoStatoDTO> getStati() {
        return stati;
    }
}
