package it.engsoft.risiko.dto;

import it.engsoft.risiko.exceptions.DatiErratiException;

import java.util.ArrayList;

public final class NuovoGiocoDTO {
    private final ArrayList<String> giocatori;
    private final long mappaId;
    private final String mod;

    public NuovoGiocoDTO(ArrayList<String> giocatori, Long mappaId, String mod) {
        if(giocatori == null)
            throw new DatiErratiException("Dati errati: lista giocatori nulla");
        this.giocatori = giocatori;

        if(mappaId < 0L)
            throw new DatiErratiException("Dati errati: id mappa non valido");
        this.mappaId = mappaId;

        if(mod == null || mod.trim().isEmpty())
            throw new DatiErratiException("Dati errati: modalita' nulla o mancante");
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
