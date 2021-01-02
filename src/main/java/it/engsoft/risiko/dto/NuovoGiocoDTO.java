package it.engsoft.risiko.dto;

import java.util.ArrayList;

public final class NuovoGiocoDTO {
    private final ArrayList<String> giocatori;
    private final long mappaId;
    private final String mod;

    public NuovoGiocoDTO(ArrayList<String> giocatori, Long mappaId, String mod) {
        if(giocatori == null)
            throw new RuntimeException("Elenco giocatori non valido.");
        this.giocatori = giocatori;

        if(mappaId < 0L)
            throw new RuntimeException("Id mappa non valido");
        this.mappaId = mappaId;

        if(mod == null || mod.trim().isEmpty())
            throw new RuntimeException("Modalità non valida.");
        this.mod = mod;
    }

    public ArrayList<String> getGiocatori() {
        return giocatori;
    }

    public long getMappaId() {
        return mappaId;
    }

    public String getMod() {
        return mod;
    }
}
