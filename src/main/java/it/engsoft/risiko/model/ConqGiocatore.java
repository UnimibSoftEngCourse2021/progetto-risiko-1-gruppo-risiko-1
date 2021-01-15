package it.engsoft.risiko.model;

import it.engsoft.risiko.exceptions.ModelDataException;

/**
 * Un obiettivo che impone ad un giocatore di distruggere tutte le armate di un altro giocatore. Se ciò non è possibile,
 * l'obiettivo di ripiego diventa conquistare un certo numero di territori della mappa.
 */
public class ConqGiocatore extends Obiettivo {
    private final Giocatore target;
    private final ConqTerritori obSecondario;

    /**
     * Crea un nuovo obiettivo di conquista giocatore.
     * @param target il giocatore da sconfiggere
     * @param obSecondario l'obiettivo secondario da raggiungere qualora il primo non fosse possibile
     */
    public ConqGiocatore(Giocatore target, ConqTerritori obSecondario) {
        this.target = target;
        this.obSecondario = obSecondario;
    }

    @Override
    public String getDescrizione() {
        return "Devi distruggere le armate del giocatore " + target.getNome() + ". Se sei tu quel giocatore, o se" +
                " è già stato sconfitto da altri: " + obSecondario.getDescrizione();
    }

    /**
     * Ritorna il giocatore da sconfiggere.
     * @return il giocatore
     */
    public Giocatore getTarget() {
        return target;
    }

    @Override
    public boolean raggiunto(Giocatore giocatore) {
        if(giocatore == null)
            throw new ModelDataException("Giocatore in ConqGiocatore.raggiunto non valido");

        if (target.isEliminato()) {
            if (target.getUccisore().equals(giocatore))
                return true;
            else
                return obSecondario.raggiunto(giocatore);
        }

        return false;
    }
}
