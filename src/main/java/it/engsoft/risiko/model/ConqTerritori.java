package it.engsoft.risiko.model;

public class ConqTerritori extends Obiettivo {
    private int numeroTerritori;
    private int numeroArmate;

    public int getNumeroTerritori() {
        return numeroTerritori;
    }

    public void setNumeroTerritori(int numeroTerritori) {
        if(numeroTerritori <= 0)
            throw new RuntimeException();

        this.numeroTerritori = numeroTerritori;
    }

    public int getNumeroArmate() {
        return numeroArmate;
    }

    public void setNumeroArmate(int numeroArmate) {
        if(numeroArmate <= 0)
            throw new RuntimeException();

        this.numeroArmate = numeroArmate;
    }

    public ConqTerritori() {}

    public boolean raggiunto() {
        return true;
    }
}
