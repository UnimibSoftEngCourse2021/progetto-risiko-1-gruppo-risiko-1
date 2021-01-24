package it.engsoft.risiko.data.model;

import it.engsoft.risiko.exceptions.ModelDataException;
import it.engsoft.risiko.exceptions.MossaIllegaleException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Rappresenta un singolo combattimento tra due stati fra loro confinanti ed appartenenti a giocatori diversi.
 * Si occupa di memorizzare tutte le informazioni relative al combattimento, di gestire il suo svolgimento, e di
 * modificare le variabili degli stati e dei giocatori in accordo con i risultati.
 */
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

    /**
     * Crea un nuovo combattimento specificando i due stati combattenti e il numero di armate attaccanti.
     * @param statoAttaccante lo stato attaccante
     * @param statoDifensore lo stato difensore
     * @param armateAttaccante il numero di armate attaccanti
     */
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

    /**
     * Esegue il combattimento, simulando il lancio dei dadi e adattando le variabili di giocatori e stati in accordo con
     * i risultati. Può essere eseguito solo una volta per ogni istanza.
     * @param armateDifensore il numero di armate con cui il giocatore intende difendersi
     */
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

    /**
     * Ritorna true se il territorio attaccante ha conquistato il difensore, false altrimenti (o se il combattimento non si
     * è ancora svolto).
     * @return true se è avvenuta una conquista
     */
    public boolean getConquista() {
        return conquista;
    }

    /**
     * Ritorna il numero di vittime tra le armate attaccanti, 0 se il combattimento non si è ancora svolto.
     * @return il numero di vittime tra le armate attaccanti
     */
    public int getVittimeAttaccante() {
        return vittimeAttaccante;
    }

    /**
     * Ritorna il numero di vittime tra le armate difensori, 0 se il combattimento non si è ancora svolto.
     * @return il numero di vittime tra le armate difensori
     */
    public int getVittimeDifensore() {
        return vittimeDifensore;
    }

    /**
     * Ritorna il numero di armate attaccanti.
     * @return il numero di armate attaccanti
     */
    public int getArmateAttaccante() { return armateAttaccante; }

    /**
     * Ritorna gli esiti del lancio dei dadi dell'attaccante.
     * @return gli esiti del lancio dei dadi
     */
    public List<Integer> getTiriAttaccante() {
        return tiriAttaccante;
    }

    /**
     * Ritorna gli esiti del lancio dei dadi del difensore.
     * @return gli esiti del lancio dei dadi
     */
    public List<Integer> getTiriDifensore() {
        return tiriDifensore;
    }

    /**
     * Ritorna lo stato attaccante.
     * @return lo stato attaccante
     */
    public Stato getStatoAttaccante() { return statoAttaccante; }

    /**
     * Ritorna lo stato difensore.
     * @return lo stato difensore
     */
    public Stato getStatoDifensore() { return statoDifensore; }

    /**
     * Ritorna true se il combattimento è già stato eseguito
     * @return true se il combattimento è già stato eseguito
     */
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
