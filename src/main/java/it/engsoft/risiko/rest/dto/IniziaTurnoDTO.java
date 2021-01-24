package it.engsoft.risiko.rest.dto;

/**
 * Classe utilizzata per gestire i dati in uscita relativi ad un turno.
 */
public final class IniziaTurnoDTO {
    private final String giocatore;
    private final int armateStati;
    private final int armateContinenti;

    /**
     * Crea un oggetto contenente le informazioni necessarie all'inizio di un turno.
     * @param giocatore Il nome del giocatore di turno
     * @param armateStati il numero di armate che spettano al giocatore in base agli stati posseduti
     * @param armateContinenti il numero di armate che spettano al giocatore in base ai continenti posseduti
     */
    public IniziaTurnoDTO(String giocatore, int armateStati, int armateContinenti) {
        this.giocatore = giocatore;
        this.armateStati = armateStati;
        this.armateContinenti = armateContinenti;
    }

    /**
     * Restituisce il nome del giocatore.
     * @return il nome del giocatore
     */
    public String getGiocatore() {
        return giocatore;
    }

    /**
     * Restituisce il numero di armate che spettano al giocatore in base agli stati posseduti
     * @return il numero di armate che spettano al giocatore in base agli stati posseduti
     */
    public int getArmateStati() {
        return armateStati;
    }

    /**
     * Restituisce il numero di armate che spettano al giocatore in base ai continenti posseduti
     * @return il numero di armate che spettano al giocatore in base ai continenti posseduti
     */
    public int getArmateContinenti() {
        return armateContinenti;
    }
}
