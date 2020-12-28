package it.engsoft.risiko.model;

public class SpostamentoStrategico {
    private final Stato partenza, destinazione;
    private final int quantita;
    private boolean eseguito;

    public SpostamentoStrategico(final Stato partenza, final Stato destinazione, final int quantita) {
        if (partenza == null || destinazione == null || quantita < 1) {
            throw new RuntimeException("Constructor parameters not valid (SpostamentoStrategico)");
        }

        this.partenza = partenza;
        this.destinazione = destinazione;
        this.quantita = quantita;
        this.eseguito = false;
    }

    public void esegui() {
        if (eseguito) {
            throw new RuntimeException("Lo spostamento è già stato eseguito");
        }

        if (!destinazione.getProprietario().equals(partenza.getProprietario())) {
            throw new RuntimeException("Si è tentato uno spostamento tra due stati di giocatori diversi!");
        }

        if (armate >= partenza.getArmate()) {
            throw new RuntimeException("Si è tentato uno spostamento non legale (troppe armate)");
        }

        partenza.rimuoviArmate(armate);
        destinazione.aggiungiArmate(armate);
    }
}
