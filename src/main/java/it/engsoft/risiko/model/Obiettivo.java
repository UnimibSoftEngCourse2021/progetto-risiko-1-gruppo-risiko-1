package it.engsoft.risiko.model;

public abstract class Obiettivo {
    private String descrizione;

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        if(descrizione == null || descrizione.trim().isEmpty())
            throw new RuntimeException();

        this.descrizione = descrizione;
    }

    public Obiettivo() {}

    public abstract boolean raggiunto();
}
