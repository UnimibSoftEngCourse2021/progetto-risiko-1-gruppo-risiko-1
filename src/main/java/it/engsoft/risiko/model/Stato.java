package it.engsoft.risiko.model;

import java.util.ArrayList;
public class Stato {
    private String nome;
    private int armate;
    private ArrayList<Stato> confinanti = new ArrayList<Stato>();
    private Giocatore proprietario;

    //nome
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        if (nome==null || nome.length()==0 )
            throw new RuntimeException("Nome stato nullo o mancante");
        this.nome = nome;
    }

    //armate
    public int getArmate() {
        return armate;
    }
    public void setArmate(int armate) {
        if(armate<=0)
            throw  new RuntimeException("Armate sullo stato nulle o invalide");
        this.armate = armate;
    }

    //stati confinanti
    public ArrayList<Stato> getConfinanti() {
        return confinanti;
    }
    //se uno stato (y) è confinante di un altro (x) dovrebbe valere anche il contrario; con l'attuale setter cio' non avviene
    public void setConfinanti(ArrayList<Stato> confinanti) {
        if(confinanti==null)
            throw new RuntimeException("Stati confinanti nulli");
        this.confinanti = confinanti;
    }

    //giocatore proprietario dello stato
    public Giocatore getProprietario() {
        return proprietario;
    }
    public void setProprietario(Giocatore proprietario) {
        if(proprietario == null)
            throw  new RuntimeException("Giocatore prorprietario dello stato nullo");
        this.proprietario = proprietario;
    }

    //ritorna true se i due stati passati in input sono confinanti, false altrimenti.
    public boolean controllaConfinanti(Stato x, Stato y){
        for(int i=0; i<x.confinanti.size(); i++){
            if(y == x.confinanti.get(i))
                return true;
        }
        return false;
    }
}
