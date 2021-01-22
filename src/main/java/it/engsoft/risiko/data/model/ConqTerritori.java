package it.engsoft.risiko.data.model;

import it.engsoft.risiko.exceptions.ModelDataException;

/**
 * Un obiettivo che impone ad un giocatore di conquistare un certo numero di territori della mappa, specificando
 * anche con quante armate ciascuno di esse deve essere occupato.
 */
public class ConqTerritori implements Obiettivo {
    private final int numeroTerritori;
    private final int numeroArmate;

    /**
     * Crea un nuovo obiettivo di conquista territori.
     * @param numeroTerritori il numero di territori da conquistare
     * @param numeroArmate il numero di armate con cui occupare ciascun territorio
     */
    public ConqTerritori(int numeroTerritori, int numeroArmate) {
        this.numeroTerritori = numeroTerritori;
        this.numeroArmate = numeroArmate;
    }

    @Override
    public String getDescrizione() {
        String desc = "Devi occupare almeno " + numeroTerritori + " territori";
        if (numeroArmate > 1)
            desc = desc + " e occupare ciascuno di essi con almeno " + numeroArmate + " armate";
        return desc;
    }

    @Override
    public boolean raggiunto(Giocatore giocatore) {
        if(giocatore == null)
            throw new ModelDataException("Giocatore in ConqTerritori.raggiunto non valido");

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
