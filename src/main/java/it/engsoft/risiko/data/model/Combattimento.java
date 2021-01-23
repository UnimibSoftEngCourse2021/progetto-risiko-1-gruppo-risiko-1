package it.engsoft.risiko.data.model;

import it.engsoft.risiko.exceptions.ModelDataException;
import it.engsoft.risiko.exceptions.MossaIllegaleException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Combattimento {
    private final Stato statoAttaccante;
    private final Stato statoDifensore;
    private final int armateAttaccante;
    private int armateDifensore;
    private boolean eseguito = false;
    private boolean conquista = false;
    private int vittimeAttaccante = 0;
    private int vittimeDifensore = 0;

    private final Random randomGenerator;

    private final List<Integer> tiriAttaccante = new ArrayList<>();
    private final List<Integer> tiriDifensore = new ArrayList<>();

    public Combattimento(Stato statoAttaccante, Stato statoDifensore, int armateAttaccante) {
        if (statoAttaccante == null || statoDifensore == null)
            throw new ModelDataException("Stati combattimento non disponibili");

        if (statiNonCompatibili(statoAttaccante, statoDifensore) ||
                armateAttaccanteNonValide(statoAttaccante, armateAttaccante)) {
            throw new MossaIllegaleException("Parametri combattimento non validi!");
        }

        this.statoAttaccante = statoAttaccante;
        this.statoDifensore = statoDifensore;
        this.armateAttaccante = armateAttaccante;
        randomGenerator = new Random();
    }

    public void esegui(final int armateDifensore) {
        if (eseguito)
            throw new ModelDataException("Combattimento già eseguito");

        if (statiNonCompatibili(statoAttaccante, statoDifensore)
                || armateAttaccanteNonValide(statoAttaccante, armateAttaccante)
                || armateDifensoreNonValide(statoDifensore, armateDifensore)) {
            throw new MossaIllegaleException("Impostazioni combattimento non valide");
        }

        this.armateDifensore = armateDifensore;

        simulazioneLancioDadi();

        conquista = (vittimeDifensore == statoDifensore.getArmate());
        statoAttaccante.rimuoviArmate(vittimeAttaccante);
        statoDifensore.rimuoviArmate(vittimeDifensore);
        if (conquista) {
            statoDifensore.getProprietario().rimuoviStato(statoDifensore);
            statoDifensore.setProprietario(statoAttaccante.getProprietario());
            statoAttaccante.getProprietario().aggiungiStato(statoDifensore);
        }

        eseguito = true;
    }

    private void simulazioneLancioDadi() {
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

    public List<Integer> getTiriAttaccante() {
        return tiriAttaccante;
    }

    public List<Integer> getTiriDifensore() {
        return tiriDifensore;
    }

    public Stato getStatoAttaccante() { return statoAttaccante; }

    public Stato getStatoDifensore() { return statoDifensore; }

    public boolean isEseguito() { return eseguito; }

    private boolean statiNonCompatibili(Stato statoAttaccante, Stato statoDifensore) {
        return statoAttaccante.getProprietario().equals(statoDifensore.getProprietario())
                || !statoAttaccante.isConfinante(statoDifensore);
    }

    private boolean armateAttaccanteNonValide(Stato statoAttaccante, int nArmate) {
        return statoAttaccante == null || statoAttaccante.getArmate() <= nArmate || nArmate < 1 || nArmate > 3;
    }

    private boolean armateDifensoreNonValide(Stato statoDifensore, int nArmate) {
        return statoDifensore == null || nArmate < 1 || nArmate > statoDifensore.getArmate() || nArmate > 3;
    }
}
