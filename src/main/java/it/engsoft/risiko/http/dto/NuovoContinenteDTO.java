package it.engsoft.risiko.http.dto;

import it.engsoft.risiko.exceptions.DatiErratiException;

import java.util.List;

/**
 * Classe contenente i dati inviati dall'utente relativi ad un nuovo continente.
 */
public final class NuovoContinenteDTO {
    private final String nome;
    private final int armateBonus;
    private final List<NuovoStatoDTO> stati;

    /**
     * Crea una classe contenente i dati relativi ad un nuovo continente.
     * @param nome il nome del continente
     * @param armateBonus il numero di armate bonus che garantisce il possesso del continente
     * @param stati la lista di stati presenti nel continente
     */
    public NuovoContinenteDTO(String nome, int armateBonus, List<NuovoStatoDTO> stati) {
        if(nome == null || nome.trim().isEmpty())
            throw new DatiErratiException("Dati errati: nome continente nullo o vuoto");
        this.nome = nome;

        if(armateBonus <= 0)
            throw new DatiErratiException("Dati errati: armate bonus negative o zero");
        this.armateBonus = armateBonus;

        if(stati == null || stati.size() < 4 || stati.size() > 12)
            throw new DatiErratiException("Dati errati: numero di stati per continente non valido");

        this.stati = stati;
    }

    /**
     * Restituisce il nome del continente.
     * @return il nome del continente
     */
    public String getNome() {
        return nome;
    }

    /**
     * Restituisce il numero di armate bonus che garantisce il possesso del continente.
     * @return il numero di armate bonus che garantisce il possesso del continente
     */
    public int getArmateBonus() {
        return armateBonus;
    }

    /**
     * Restituisce la lista di stati presenti nel continente.
     * @return lista di stati in formato NuovoStatoDTO
     */
    public List<NuovoStatoDTO> getStati() {
        return stati;
    }
}
