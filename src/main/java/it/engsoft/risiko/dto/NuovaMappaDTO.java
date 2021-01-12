package it.engsoft.risiko.dto;

import java.util.List;

public class NuovaMappaDTO {
    private String nome;
    private String descrizione;
    private int numMinGiocatori;
    private int numMaxGiocatori;
    private List<NuovoContinenteDTO> continenti;

    public NuovaMappaDTO(String nome, String descrizione, int numMinGiocatori, int numMaxGiocatori, List<NuovoContinenteDTO> continenti) {
        this.nome = nome;
        this.descrizione = descrizione;
        this.numMinGiocatori = numMinGiocatori;
        this.numMaxGiocatori = numMaxGiocatori;
        this.continenti = continenti;
    }

    public String getNome() {
        return nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public int getNumMinGiocatori() {
        return numMinGiocatori;
    }

    public int getNumMaxGiocatori() {
        return numMaxGiocatori;
    }

    public List<NuovoContinenteDTO> getContinenti() {
        return continenti;
    }
}
