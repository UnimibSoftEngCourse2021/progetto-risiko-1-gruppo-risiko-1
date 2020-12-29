package it.engsoft.risiko.model;

import java.util.ArrayList;

public class Continente {
    private String nome;
    private int armateBonus;
    private ArrayList<Stato> stati = new ArrayList<Stato>();

    // TODO: metodi add/remove per stati anziché setter

    //nome
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty())
            throw new RuntimeException("Nome continente nullo o mancante");
        this.nome = nome;
    }

    //armate bonus
    public int getArmateBonus() {
        return armateBonus;
    }

    public void setArmateBonus(int armateBonus) {
        if (armateBonus <= 0)
            throw new RuntimeException("Armate bonus zero o negative");
        this.armateBonus = armateBonus;
    }

    //stati
    public ArrayList<Stato> getStati() {
        return stati;
    }

    public void setStati(ArrayList<Stato> stati) {
        if (stati == null)
            throw new RuntimeException("Stati apparteneti al continente nulli");
        this.stati = stati;
    }
}
