package it.engsoft.risiko.dto;

import it.engsoft.risiko.exceptions.DatiErratiException;

import java.util.List;

public class NuovoStatoDTO {
    private final String nome;
    private final List<String> confinanti;

    public NuovoStatoDTO(String nome, List<String> confinanti) {
        if(nome == null || nome.trim().isEmpty())
            throw new DatiErratiException();
        this.nome = nome;

        if(confinanti == null || confinanti.size() == 0)
            throw new DatiErratiException();
        this.confinanti = confinanti;
    }

    public String getNome() {
        return nome;
    }

    public List<String> getConfinanti() {
        return confinanti;
    }
}
