package it.engsoft.risiko;

import it.engsoft.risiko.dto.NuovaMappaDTO;
import it.engsoft.risiko.dto.NuovoContinenteDTO;
import it.engsoft.risiko.dto.NuovoStatoDTO;
import it.engsoft.risiko.model.Mappa;
import it.engsoft.risiko.model.Stato;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Funzioni utili per il testing
 */
public class Utils {

    /**
     * Prende una mappa esistente e la formatta come DTO, modificandone solo le informazioni generali, in modo da poterne
     * testare l'inserimento).
     * @param mappa la mappa da formattare
     * @param nome il nome da assegnare alla nuova mappa
     * @param descrizione la descrizione da assegnare alla nuova mappa
     * @param numMinGiocatori il numero min di giocatori della nuova mappa
     * @param numMaxGiocatori il numero max di giocatori della nuova mappa
     * @return la mappa formattata
     */
    public NuovaMappaDTO dtoFromMappa(Mappa mappa, String nome, String descrizione, int numMinGiocatori,
                                             int numMaxGiocatori) {
        List<NuovoContinenteDTO> nuoviContinenti = mappa.getContinenti().stream().map(c -> new NuovoContinenteDTO(
                c.getNome(),
                c.getArmateBonus(),
                c.getStati().stream().map(s -> new NuovoStatoDTO(
                        s.getNome(),
                        s.getConfinanti().stream().map(Stato::getNome).collect(Collectors.toList())
                )).collect(Collectors.toList())
        )).collect(Collectors.toList());

        return new NuovaMappaDTO(nome, descrizione, numMinGiocatori, numMaxGiocatori, nuoviContinenti);
    }
}
