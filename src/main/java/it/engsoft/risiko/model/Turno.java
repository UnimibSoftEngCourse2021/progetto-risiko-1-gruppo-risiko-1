package it.engsoft.risiko.model;

public class Turno {
    private final int numero;
    private Fase fase;
    private final Giocatore giocatoreAttivo;
    private Combattimento combattimentoInCorso;

    public enum Fase {RINFORZI, COMBATTIMENTI, SPOSTAMENTO}

    ;

    public Turno(final Giocatore giocatore, final int numero) {
        this.numero = numero;
        this.giocatoreAttivo = giocatore;
        this.combattimentoInCorso = null;
        this.fase = Fase.RINFORZI;
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

    public Giocatore getGiocatoreAttivo() {
        return giocatoreAttivo;
    }

    //public Rinforzo getRinforzo() {
        //return rinforzo;
    //}

    //public void setRinforzo(Rinforzo rinforzo) {
       // this.rinforzo = rinforzo;
    //}

    //public SpostamentoStrategico getSpostamentoStrategico() {
      //  return spostamentoStrategico;
    //}

    //public void setSpostamentoStrategico(SpostamentoStrategico spostamentoStrategico) {
      //  this.spostamentoStrategico = spostamentoStrategico;
    //}
}
