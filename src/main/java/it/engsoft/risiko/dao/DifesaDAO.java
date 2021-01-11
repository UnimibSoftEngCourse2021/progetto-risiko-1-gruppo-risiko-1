package it.engsoft.risiko.dao;

import it.engsoft.risiko.model.Combattimento;

import java.util.ArrayList;

public class DifesaDAO {
    private ArrayList<Integer> dadoAtt;
    private ArrayList<Integer> dadoDif;
    private int vittimeAtt;
    private int vittimeDif;
    private boolean obiettivoRaggiuntoAtt;
    private boolean vittoriaAtt;
    private boolean eliminato;

    public DifesaDAO(Combattimento combattimento, boolean obiettivoRaggiuntoAtt, boolean difensoreEliminato) {
        this.dadoAtt = combattimento.getTiriAttaccante();
        this.dadoDif = combattimento.getTiriDifensore();
        this.vittimeAtt = combattimento.getVittimeAttaccante();
        this.vittimeDif = combattimento.getVittimeDifensore();
        this.vittoriaAtt = combattimento.getConquista();
        this.obiettivoRaggiuntoAtt = obiettivoRaggiuntoAtt;
        this.eliminato = difensoreEliminato;
    }

    public ArrayList<Integer> getDadoAtt() {
        return dadoAtt;
    }

    public ArrayList<Integer> getDadoDif() {
        return dadoDif;
    }

    public int getVittimeAtt() {
        return vittimeAtt;
    }

    public int getVittimeDif() {
        return vittimeDif;
    }

    public boolean isVittoriaAtt() {
        return vittoriaAtt;
    }

    public boolean isObiettivoRaggiuntoAtt() {
        return obiettivoRaggiuntoAtt;
    }

    public boolean isEliminato() { return eliminato; }
}