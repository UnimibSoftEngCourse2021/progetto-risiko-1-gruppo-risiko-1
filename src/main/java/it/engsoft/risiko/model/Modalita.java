package it.engsoft.risiko.model;

public enum Modalita {
    VELOCE,
    RIDOTTA,
    COMPLETA;

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
