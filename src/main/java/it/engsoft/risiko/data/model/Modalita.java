package it.engsoft.risiko.data.model;

/**
 * Rappresenta una modalità di gioco.
 */
public enum Modalita {
    /** modalità veloce: metà degli stati della mappa sono usati */
    VELOCE,
    /** modalità ridotta: 2/3 degli stati della mappa sono usati */
    RIDOTTA,
    /** modalità completa: tutti gli stati della mappa sono usati */
    COMPLETA;

    /**
     * Riceve in input una stringa e cerca di associarla ad una modalità. Se l'associazione fallisce, viene ritornata
     * la modalità completa.
     * @param modalita la stringa che rappresenta la modalità
     * @return la modalità associata
     */
    public static Modalita valutaModalita(String modalita) {
        if (modalita.equalsIgnoreCase(Modalita.COMPLETA.toString())) {
            return Modalita.COMPLETA;
        } else if (modalita.equalsIgnoreCase(Modalita.RIDOTTA.toString())) {
            return Modalita.RIDOTTA;
        } else if (modalita.equalsIgnoreCase(Modalita.VELOCE.toString())) {
            return Modalita.VELOCE;
        }

        return Modalita.COMPLETA;
    }
}
