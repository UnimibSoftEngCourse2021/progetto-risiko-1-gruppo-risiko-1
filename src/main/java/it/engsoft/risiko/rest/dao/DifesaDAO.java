package it.engsoft.risiko.rest.dao;

import it.engsoft.risiko.data.model.Combattimento;

import java.util.List;

public class DifesaDAO {
    private final List<Integer> dadoAtt;
    private final List<Integer> dadoDif;
    private final int vittimeAtt;
    private final int vittimeDif;
    private final boolean obiettivoRaggiuntoAtt;
    private final boolean vittoriaAtt;
    private final boolean eliminato;

    public DifesaDAO(Combattimento combattimento, boolean obiettivoRaggiuntoAtt, boolean difensoreEliminato) {
        this.dadoAtt = combattimento.getTiriAttaccante();
        this.dadoDif = combattimento.getTiriDifensore();
        this.vittimeAtt = combattimento.getVittimeAttaccante();
        this.vittimeDif = combattimento.getVittimeDifensore();
        this.vittoriaAtt = combattimento.getConquista();
        this.obiettivoRaggiuntoAtt = obiettivoRaggiuntoAtt;
        this.eliminato = difensoreEliminato;
    }

    public List<Integer> getDadoAtt() {
        return dadoAtt;
    }

    public List<Integer> getDadoDif() {
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