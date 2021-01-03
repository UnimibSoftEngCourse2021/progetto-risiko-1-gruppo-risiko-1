package it.engsoft.risiko.model;

public class ConqGiocatore extends Obiettivo {
    private final Giocatore target;
    private final ConqTerritori obSecondario;

    public ConqGiocatore(Giocatore target, ConqTerritori obSecondario) {
        this.target = target;
        this.obSecondario = obSecondario;
    }

    public Giocatore getTarget() {
        return target;
    }

    public ConqTerritori getObSecondario() {
        return obSecondario;
    }

    public boolean raggiunto(Giocatore giocatore) {
        if(giocatore == null)
            throw new RuntimeException("Giocatore non valido");

        if (target.isEliminato()) {
            if (target.getUccisore().equals(giocatore))
                return true;
            else
                return obSecondario.raggiunto(giocatore);
        }

        return false;
    }
}
