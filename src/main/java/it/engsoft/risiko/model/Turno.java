package it.engsoft.risiko.model;

public class Turno {
    private final int numero;
    private Fase fase;
    private Combattimento combattimentoInCorso;
    private boolean conquista;

    public enum Fase {NULL, RINFORZI, COMBATTIMENTI, SPOSTAMENTO}

    public Turno(final int numero) {
        this.numero = numero;
        this.combattimentoInCorso = null;
        this.fase = Fase.NULL;
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


    //TODO:: pulire metodi se inutili
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
