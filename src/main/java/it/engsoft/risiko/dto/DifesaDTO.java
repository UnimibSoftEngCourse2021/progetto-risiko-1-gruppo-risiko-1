package it.engsoft.risiko.dto;

public final class DifesaDTO {
    private final String giocatore;
    private final int armate;

    public DifesaDTO(String giocatore, int armate) {
        if(giocatore == null || giocatore.trim().isEmpty())
            throw new RuntimeException("Nome giocatore non valido.");
        this.giocatore = giocatore;

        if(armate <= 0)
            throw new RuntimeException("Numero armate non valido.");
        this.armate = armate;
    }

    public String getGiocatore() {
        return giocatore;
    }

    public int getArmate() {
        return armate;
    }
}
