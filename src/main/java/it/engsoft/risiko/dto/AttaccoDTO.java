package it.engsoft.risiko.dto;

public final class AttaccoDTO {
    private final String giocatore;
    private final long attaccante;
    private final long difensore;
    private final int armate;

    public AttaccoDTO(String giocatore, Long attaccante, Long difensore, int armate) {
        if(giocatore == null || giocatore.trim().isEmpty())
            throw new RuntimeException("Nome giocatore non valido");
        this.giocatore = giocatore;

        if(attaccante < 0L)
            throw new RuntimeException("Id attaccante non valido");
        this.attaccante = attaccante;

        if(difensore < 0L)
            throw new RuntimeException("Id difensore non valido");
        this.difensore = difensore;

        if(armate <= 0)
            throw new RuntimeException("numero armate non valido");
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
