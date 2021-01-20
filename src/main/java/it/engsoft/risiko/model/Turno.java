package it.engsoft.risiko.model;

public class Turno {
    private final int numero;
    private Fase fase;
    private Combattimento combattimentoInCorso;
    private boolean conquista;

    public enum Fase {INIZIALIZZAZIONE, RINFORZI, COMBATTIMENTI, SPOSTAMENTO}

    public Turno(final int numero) {
        this.numero = numero;
        this.combattimentoInCorso = null;
        this.fase = Fase.INIZIALIZZAZIONE;
        conquista = false;
    }

    // getter e setter
    public Combattimento getCombattimentoInCorso() {
        return combattimentoInCorso;
    }

    public int getNumero() {
        return numero;
    }

    public void setCombattimentoInCorso(Combattimento combattimentoInCorso) {
        this.combattimentoInCorso = combattimentoInCorso;
    }

    public Fase getFase() {
        return fase;
    }

    public void setFase(Fase fase) {
        this.fase = fase;
    }

    public void registraConquista() {
        conquista = true;
    }

    public boolean conquistaAvvenuta() {
        return conquista;
    }

}
