package it.engsoft.risiko.rest.DTO;

import it.engsoft.risiko.exceptions.DatiErratiException;

public final class AttaccoDTO {
    private final String giocatore;
    private final long attaccante;
    private final long difensore;
    private final int armate;

    public AttaccoDTO(String giocatore, Long attaccante, Long difensore, int armate) {
        if(giocatore == null || giocatore.trim().isEmpty())
            throw new DatiErratiException("Dati errati: giocatore nullo o vuoto");
        this.giocatore = giocatore;

        if(attaccante < 0L)
            throw new DatiErratiException("Dati errati: id attaccante non valido");
        this.attaccante = attaccante;

        if(difensore < 0L)
            throw new DatiErratiException("Dati errati: id difensore non valido");
        this.difensore = difensore;

        if(armate <= 0)
            throw new DatiErratiException("Dati errati: numero armate negativo");
        this.armate = armate;
    }

    public String getGiocatore() {
        return giocatore;
    }

    public long getAttaccante() {
        return attaccante;
    }

    public long getDifensore() {
        return difensore;
    }

    public int getArmate() {
        return armate;
    }
}
