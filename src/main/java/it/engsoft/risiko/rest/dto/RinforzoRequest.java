package it.engsoft.risiko.rest.dto;

import it.engsoft.risiko.exceptions.DatiErratiException;

import java.util.Map;
import java.util.stream.Collectors;

public final class RinforzoRequest {
    private final String giocatore;
    private final Map<Long, Integer> rinforzi;

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

    public String getGiocatore() {
        return giocatore;
    }

    public Map<Long, Integer> getRinforzi() {
        return rinforzi;
    }
}
