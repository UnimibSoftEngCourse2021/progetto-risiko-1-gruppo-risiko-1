package it.engsoft.risiko.rest.DTO;

import it.engsoft.risiko.exceptions.DatiErratiException;

import java.util.List;

public final class NuovoContinenteDTO {
    private final String nome;
    private final int armateBonus;
    private final List<NuovoStatoDTO> stati;

    public NuovoContinenteDTO(String nome, int armateBonus, List<NuovoStatoDTO> stati) {
        if(nome == null || nome.trim().isEmpty())
            throw new DatiErratiException("Dati errati: nome continente nullo o vuoto");
        this.nome = nome;

        if(armateBonus <= 0)
            throw new DatiErratiException("Dati errati: armate bonus negative o zero");
        this.armateBonus = armateBonus;

        if(stati == null || stati.size() < 4 || stati.size() > 12)
            throw new DatiErratiException("Dati errati: numero di stati per continente non valido");

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
