package it.engsoft.risiko.dto;

import java.util.Map;
import java.util.stream.Collectors;

public final class RinforzoDTO {
    private final String giocatore;
    private final Map<Long, Integer> rinforzi;

    public RinforzoDTO(String giocatore, Map<String, Integer> rinforzi) {
        this.giocatore = giocatore;

        this.rinforzi = rinforzi.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> Long.parseLong(e.getKey()),
                        e -> e.getValue()
                ));
    }

    public String getGiocatore() {
        return giocatore;
    }

    public Map<Long, Integer> getRinforzi() {
        return rinforzi;
    }
}