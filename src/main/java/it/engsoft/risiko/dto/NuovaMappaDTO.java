package it.engsoft.risiko.dto;

import it.engsoft.risiko.exceptions.DatiErratiException;

import java.util.List;

public class NuovaMappaDTO {
    private final String nome;
    private final String descrizione;
    private final int numMinGiocatori;
    private final int numMaxGiocatori;
    private final List<NuovoContinenteDTO> continenti;

    public NuovaMappaDTO(String nome, String descrizione, int numMinGiocatori, int numMaxGiocatori, List<NuovoContinenteDTO> continenti) {
        if(nome == null || nome.trim().isEmpty())
            throw new DatiErratiException();
        this.nome = nome;

        if(descrizione == null)
            throw new DatiErratiException();
        this.descrizione = descrizione;

        if(numMinGiocatori < 2 || numMaxGiocatori > 8)
            throw new DatiErratiException();
        this.numMinGiocatori = numMinGiocatori;
        this.numMaxGiocatori = numMaxGiocatori;

        if(continenti == null || continenti.size() < 3)
            throw new DatiErratiException();
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
