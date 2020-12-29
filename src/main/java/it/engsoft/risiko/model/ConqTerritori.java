package it.engsoft.risiko.model;

public class ConqTerritori extends Obiettivo {
    private int numeroTerritori;
    private int numeroArmate;

    public int getNumeroTerritori() {
        return numeroTerritori;
    }

    public void setNumeroTerritori(int numeroTerritori) {
        if (numeroTerritori <= 0)
            throw new RuntimeException();

        this.numeroTerritori = numeroTerritori;
    }

    public int getNumeroArmate() {
        return numeroArmate;
    }

    public void setNumeroArmate(int numeroArmate) {
        if (numeroArmate <= 0)
            throw new RuntimeException();

        this.numeroArmate = numeroArmate;
    }

    public ConqTerritori() {
    }

    public boolean raggiunto(Giocatore giocatore) {
        if(giocatore == null)
            throw new RuntimeException("Giocatore non valido");

        return giocatore.getStati().size() >= numeroTerritori && armatesufficienti(giocatore);
    }

    private boolean armatesufficienti(Giocatore giocatore) {
        for (Stato stato : giocatore.getStati()) {
            if (stato.getArmate() < numeroArmate)
                return false;
        }

        return true;
    }
}
