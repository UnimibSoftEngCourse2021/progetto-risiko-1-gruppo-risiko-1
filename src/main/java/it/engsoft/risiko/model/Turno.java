package it.engsoft.risiko.model;

public class Turno {
    private final int numero;
    private Combattimento combattimentoInCorso;
    private Fase fase;
    private final Giocatore giocatoreAttivo;

    private enum Fase { RINFORZI, COMBATTIMENTI, SPOSTAMENTO };

    public Turno(final Giocatore giocatore, final int numero) {
        this.numero = numero;
        this.giocatoreAttivo = giocatore;
        this.combattimentoInCorso = null;
        this.fase = Fase.RINFORZI;
    }

    public int getNumero() {
        return numero;
    }
}
