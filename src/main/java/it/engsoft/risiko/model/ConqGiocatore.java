package it.engsoft.risiko.model;

public class ConqGiocatore {
    private Giocatore target;
    private ConqTerritori obSecondario;

    public Giocatore getTarget() {
        return target;
    }

    public void setTarget(Giocatore target) {
        if (target == null)
            throw new RuntimeException();
        this.target = target;
    }

    public ConqTerritori getObSecondario() {
        return obSecondario;
    }

    public void setObSecondario(ConqTerritori obSecondario) {
        if (obSecondario == null)
            throw new RuntimeException();

        this.obSecondario = obSecondario;
    }

    public ConqGiocatore() {
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
