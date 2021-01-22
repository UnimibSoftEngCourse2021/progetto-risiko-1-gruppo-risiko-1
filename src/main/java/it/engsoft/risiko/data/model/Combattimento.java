package it.engsoft.risiko.data.model;

import it.engsoft.risiko.exceptions.ModelDataException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Combattimento {
    private final Stato statoAttaccante, statoDifensore;
    private final int armateAttaccante;
    private boolean conquista;
    private int vittimeAttaccante, vittimeDifensore;

    private final Random randomGenerator;

    private final ArrayList<Integer> tiriAttaccante, tiriDifensore;

    public Combattimento(Stato statoAttaccante, Stato statoDifensore, int armateAttaccante) {
        if (statiNonCompatibili(statoAttaccante, statoDifensore) || armateAttaccanteNonValide(statoAttaccante, armateAttaccante)) {
            throw new ModelDataException("Combattimento constructor: not valid parameters!");
        }

        this.statoAttaccante = statoAttaccante;
        this.statoDifensore = statoDifensore;
        this.armateAttaccante = armateAttaccante;
        randomGenerator = new Random();
        tiriAttaccante = new ArrayList<>();
        tiriDifensore = new ArrayList<>();
    }

    private boolean statiNonCompatibili(Stato statoAttaccante, Stato statoDifensore) {
        return statoAttaccante == null
                || statoDifensore == null
                || statoAttaccante.getProprietario().equals(statoDifensore.getProprietario())
                || !statoAttaccante.isConfinante(statoDifensore);
    }

    private boolean armateAttaccanteNonValide(Stato statoAttaccante, int nArmate) {
        return statoAttaccante == null || statoAttaccante.getArmate() <= nArmate || nArmate < 1 || nArmate > 3;
    }

    private boolean armateDifensoreNonValide(Stato statoDifensore, int nArmate) {
        return statoDifensore == null || nArmate < 1 || nArmate > statoDifensore.getArmate() || nArmate > 3;
    }

    public void simulaCombattimento(final int armateDifensore) {
        if (statiNonCompatibili(statoAttaccante, statoDifensore)
                || armateAttaccanteNonValide(statoAttaccante, armateAttaccante)
                || armateDifensoreNonValide(statoDifensore, armateDifensore)) {
            throw new ModelDataException("Impostazioni simulaCombattimento non valide");
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

    public int getArmateAttaccante() { return armateAttaccante; }

    public ArrayList<Integer> getTiriAttaccante() {
        return tiriAttaccante;
    }

    public ArrayList<Integer> getTiriDifensore() {
        return tiriDifensore;
    }

    public Stato getStatoAttaccante() { return statoAttaccante; }

    public Stato getStatoDifensore() { return statoDifensore; }

}
