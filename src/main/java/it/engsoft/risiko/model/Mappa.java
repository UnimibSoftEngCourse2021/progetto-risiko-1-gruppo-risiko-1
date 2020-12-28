package it.engsoft.risiko.model;

import java.util.ArrayList;
public class Mappa {
    private String nome;
    private String descrizione;
    private int numMinGiocatori;
    private int numMaxGiocatori;
    private ArrayList<Continente>  continenti = new ArrayList<Continente>();

    //nome
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        if (nome == null || nome.length() == 0)
            throw new RuntimeException("Nome mappa nullo o mancante");
        this.nome = nome;
    }

    //descrizione
    public String getDescrizione() {
        return descrizione;
    }
    public void setDescrizione(String descrizione) {
        if (descrizione == null)
            throw new RuntimeException("Descrizione mappa nulla");
        this.descrizione = descrizione;
    }

    //numero minimo giocatori
    public int getNumMinGiocatori() {
        return numMinGiocatori;
    }
    public void setNumMinGiocatori(int numMinGiocatori) {
        if(numMinGiocatori<2)
            throw new RuntimeException("Numero giocatori minimo inferiore a 2");
        this.numMinGiocatori = numMinGiocatori;
    }

    //numero massimo giocatore
    public int getNumMaxGiocatori() {
        return numMaxGiocatori;
    }
    public void setNumMaxGiocatori(int numMaxGiocatori) {
        if(numMaxGiocatori<2)
            throw new RuntimeException("Numero giocatori massimo inferiore a 2");
        this.numMaxGiocatori = numMaxGiocatori;
    }

    //continenti
    public ArrayList<Continente> getContinenti() {
        return continenti;
    }
    public void setContinenti(ArrayList<Continente> continenti) {
        if (continenti == null)
            throw new RuntimeException("Continenti appartenenti alla mappa nulli");
        this.continenti = continenti;
    }
}
