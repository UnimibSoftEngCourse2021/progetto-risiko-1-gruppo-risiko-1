package it.engsoft.risiko.model;

public class ConqGiocatore {
    private Giocatore giocatore;
    private Giocatore target;
    private ConqTerritori obSecondario;

    public Giocatore getGiocatore() {
        return giocatore;
    }

    public void setGiocatore(Giocatore giocatore) {
        if(giocatore == null)
            throw new RuntimeException();

        this.giocatore = giocatore;
    }

    public ConqTerritori getObSecondario() {
        return obSecondario;
    }

    public void setObSecondario(ConqTerritori obSecondario) {
        if(obSecondario == null)
            throw new RuntimeException();

        this.obSecondario = obSecondario;
    }

    public ConqGiocatore() {}

    public boolean raggiunto() {
        if(target.isEliminato()) {
            if(target.getUccisore() == this.giocatore)
                return true;
            else
                return obSecondario.raggiunto();
        }

        return false;
    }
}
