package it.engsoft.risiko.rest.dto;

import it.engsoft.risiko.exceptions.DatiErratiException;

import java.util.List;

public class NuovoStatoDTO {
    private final String nome;
    private final List<String> confinanti;

    public NuovoStatoDTO(String nome, List<String> confinanti) {
        if(nome == null || nome.trim().isEmpty())
            throw new DatiErratiException("Dati errati: nome stato nullo o mancante");
        this.nome = nome;

        if(confinanti == null || confinanti.isEmpty())
            throw new DatiErratiException("Dati errati: lo stato non ha confinanti");
        this.confinanti = confinanti;
    }

    public String getNome() {
        return nome;
    }

    public List<String> getConfinanti() {
        return confinanti;
    }
}