package it.engsoft.risiko.http.dto;

import it.engsoft.risiko.exceptions.DatiErratiException;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Classe utilizzata per gestire i dati in entrata relativi ad un rinforzo.
 */
public final class RinforzoRequest {
    private final String giocatore;
    private final Map<Long, Integer> rinforzi;

    /**
     * Crea un oggetto contenente i dati inviati dall'utente relativi ad un rinforzo.
     * @param giocatore il giocatore che effettua il rinforzo
     * @param rinforzi map contenente i nomi degli stati da rinforzare e il numero di armate da aggiungere
     */
    public RinforzoRequest(String giocatore, Map<String, Integer> rinforzi) {
        if(giocatore == null || giocatore.trim().isEmpty())
            throw new DatiErratiException("Dati errati: giocatore nullo o vuoto");
        this.giocatore = giocatore;

        this.rinforzi = rinforzi.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> Long.parseLong(e.getKey()),
                        Map.Entry::getValue
                ));
    }

    /**
     * Restituisce il nome del giocatore che effettua il rinforzo.
     * @return il nome del giocatore
     */
    public String getGiocatore() {
        return giocatore;
    }

    /**
     * Restituisce la map contenente gli id degli stati da rinforzare e il numero di armate da aggiungere.
     * @return la map contenente gli id degli stati da rinforzare e il numero di armate da aggiungere
     */
    public Map<Long, Integer> getRinforzi() {
        return rinforzi;
    }
}
