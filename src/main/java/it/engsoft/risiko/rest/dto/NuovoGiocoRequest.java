package it.engsoft.risiko.rest.dto;

import it.engsoft.risiko.exceptions.DatiErratiException;
import java.util.List;

public final class NuovoGiocoRequest {
    private final List<String> giocatori;
    private final long mappaId;
    private final String mod;
    private final boolean unicoObiettivo;

    public NuovoGiocoRequest(List<String> giocatori, Long mappaId, String mod, boolean unicoObiettivo) {
        if(giocatori == null)
            throw new DatiErratiException("Dati errati: lista giocatori nulla");
        this.giocatori = giocatori;

        if(mappaId < 0L)
            throw new DatiErratiException("Dati errati: id mappa non valido");
        this.mappaId = mappaId;

        if(mod == null || mod.trim().isEmpty())
            throw new DatiErratiException("Dati errati: modalita' nulla o mancante");
        this.mod = mod;

        this.unicoObiettivo = unicoObiettivo;
    }

    public List<String> getGiocatori() {
        return giocatori;
    }

    public long getMappaId() {
        return mappaId;
    }

    public String getMod() {
        return mod;
    }

    public boolean isUnicoObiettivo() { return unicoObiettivo; }
}
