package it.engsoft.risiko.model;

public class ConqTerritori extends Obiettivo {
    private final int numeroTerritori;
    private final int numeroArmate;

    public ConqTerritori(int numeroTerritori, int numeroArmate) {
        this.numeroTerritori = numeroTerritori;
        this.numeroArmate = numeroArmate;
    }

    public int getNumeroTerritori() {
        return numeroTerritori;
    }

    public int getNumeroArmate() {
        return numeroArmate;
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
