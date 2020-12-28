package it.engsoft.risiko.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Combattimento {
    private final Stato attaccante, difensore;
    private final int armateAttaccante;
    private int armateDifensore;

    private boolean conquista;
    private int vittimeAttaccante, vittimeDifensore;

    private final Random randomGenerator;

    private final ArrayList<Integer> tiriAttaccante, tiriDifensore;

    public Combattimento(Stato attaccante, Stato difensore, int armateAttaccante) {
        if (statiCompatibili(attaccante, difensore) && armateAttaccanteValide(attaccante, armateAttaccante)) {
            throw new RuntimeException("Combattimento constructor: not valid parameters!");
        }

        this.attaccante = attaccante;
        this.difensore = difensore;
        this.armateAttaccante = armateAttaccante;
        randomGenerator = new Random();
        tiriAttaccante = new ArrayList<>();
        tiriDifensore = new ArrayList<>();
    }

    private boolean statiCompatibili(Stato attaccante, Stato difensore) {
        return attaccante != null
                && difensore != null
                && !attaccante.getProprietario().equals(difensore.getProprietario());
    }

    private boolean armateAttaccanteValide(Stato attaccante, int nArmate) {
        return attaccante != null && attaccante.getArmate() > nArmate && nArmate >= 1 && nArmate <= 3;
    }

    private boolean armateDifensoreValide(Stato difensore, int nArmate) {
        return difensore != null && nArmate >= 1 && nArmate <= difensore.getArmate() && nArmate <= 3;
    }

    public void simulaCombattimento(final int armateDifensore) {
        if (!statiCompatibili(attaccante, difensore)
                || !armateAttaccanteValide(attaccante, armateAttaccante)
                || !armateDifensoreValide(difensore, armateDifensore)) {
            throw new RuntimeException("Impostazioni combattimento non valide");
        }

        ArrayList<Integer> tiriAttaccante = new ArrayList<>();
        ArrayList<Integer> tiriDifensore = new ArrayList<>();

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
        this.conquista = vittimeDifensore == difensore.getArmate();
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
}
