package it.engsoft.risiko.data.model;

import it.engsoft.risiko.exceptions.ModelDataException;

public class SpostamentoStrategico {
    private final Stato partenza;
    private final Stato destinazione;
    private final int quantita;
    private boolean eseguito;

    public SpostamentoStrategico(final Stato partenza, final Stato destinazione, final int quantita) {
        if (partenza == null
                || destinazione == null
                || quantita < 1
                || partenza.equals(destinazione)
                || !partenza.isConfinante(destinazione)) {
            throw new ModelDataException("Constructor parameters not valid (SpostamentoStrategico)");
        }

        this.partenza = partenza;
        this.destinazione = destinazione;
        this.quantita = quantita;
        this.eseguito = false;
    }

    public void esegui() {
        if (eseguito) {
            throw new ModelDataException("Lo spostamento è già stato eseguito");
        }

        if (!destinazione.getProprietario().equals(partenza.getProprietario())) {
            throw new ModelDataException("Si è tentato uno spostamento tra due stati di giocatori diversi!");
        }

        if (quantita >= partenza.getArmate()) {
            throw new ModelDataException("Si è tentato uno spostamento non legale (troppe armate)");
        }

        partenza.rimuoviArmate(quantita);
        destinazione.aggiungiArmate(quantita);
        eseguito = true;
    }

    public Stato getPartenza() { return partenza; }

    public int getQuantita() { return quantita; }
}
