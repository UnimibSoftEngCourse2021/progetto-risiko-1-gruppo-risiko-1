package it.engsoft.risiko.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Combattimento {
    private final Stato statoAttaccante, statoDifensore;
    private final int armateAttaccante;
    private int armateDifensore;

    private boolean conquista;
    private int vittimeAttaccante, vittimeDifensore;

    private final Random randomGenerator;

    private final ArrayList<Integer> tiriAttaccante, tiriDifensore;

    private SpostamentoStrategico spostamentoAttacco;

    public Combattimento(Stato statoAttaccante, Stato statoDifensore, int armateAttaccante) {
        if (!statiCompatibili(statoAttaccante, statoDifensore) || !armateAttaccanteValide(statoAttaccante, armateAttaccante)) {
            throw new RuntimeException("Combattimento constructor: not valid parameters!");
        }

        this.statoAttaccante = statoAttaccante;
        this.statoDifensore = statoDifensore;
        this.armateAttaccante = armateAttaccante;
        randomGenerator = new Random();
        tiriAttaccante = new ArrayList<>();
        tiriDifensore = new ArrayList<>();
    }

    private boolean statiCompatibili(Stato statoAttaccante, Stato statoDifensore) {
        return statoAttaccante != null
                && statoDifensore != null
                && !statoAttaccante.getProprietario().equals(statoDifensore.getProprietario())
                && statoAttaccante.isConfinante(statoDifensore);
    }

    private boolean armateAttaccanteValide(Stato statoAttaccante, int nArmate) {
        return statoAttaccante != null && statoAttaccante.getArmate() > nArmate && nArmate >= 1 && nArmate <= 3;
    }

    private boolean armateDifensoreValide(Stato statoDifensore, int nArmate) {
        return statoDifensore != null && nArmate >= 1 && nArmate <= statoDifensore.getArmate() && nArmate <= 3;
    }

    public void simulaCombattimento(final int armateDifensore) {
        if (!statiCompatibili(statoAttaccante, statoDifensore)
                || !armateAttaccanteValide(statoAttaccante, armateAttaccante)
                || !armateDifensoreValide(statoDifensore, armateDifensore)) {
            throw new RuntimeException("Impostazioni combattimento non valide");
        }


        for (int i = 0; i < armateAttaccante; i++) {
            tiriAttaccante.add(lanciaDado());
        }
        for (int i = 0; i < armateDifensore; i++) {
            tiriDifensore.add(lanciaDado());
        }

        Collections.sort(tiriAttaccante);
        Collections.reverse(tiriAttaccante);

        Collections.sort(tiriDifensore);
        Collections.reverse(tiriDifensore);

        int combattimenti = Math.min(armateDifensore, armateAttaccante);
        vittimeAttaccante = 0;
        vittimeDifensore = 0;
        for (int i = 0; i < combattimenti; i++) {
            if (tiriAttaccante.get(i) > tiriDifensore.get(i)) {
                vittimeDifensore++;
            } else {
                vittimeAttaccante++;
            }
        }
        this.conquista = (vittimeDifensore == statoDifensore.getArmate());
    }

    private Integer lanciaDado() {
        return randomGenerator.nextInt(6) + 1;
    }

    public boolean getConquista() {
        return conquista;
    }

    public int getVittimeAttaccante() {
        return vittimeAttaccante;
    }

    public int getVittimeDifensore() {
        return vittimeDifensore;
    }

    public ArrayList<Integer> getTiriAttaccante() {
        return tiriAttaccante;
    }

    public ArrayList<Integer> getTiriDifensore() {
        return tiriDifensore;
    }

    public SpostamentoStrategico getSpostamentoAttacco() { return spostamentoAttacco; }

    public void setSpostamentoAttacco(SpostamentoStrategico spostamentoAttacco) {
        this.spostamentoAttacco = spostamentoAttacco;
    }
}
