package it.engsoft.risiko;

import it.engsoft.risiko.rest.DTO.NuovaMappaDTO;
import it.engsoft.risiko.rest.DTO.NuovoContinenteDTO;
import it.engsoft.risiko.rest.DTO.NuovoStatoDTO;
import it.engsoft.risiko.data.model.*;
import it.engsoft.risiko.data.repository.MappaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Funzioni utili per il testing
 */
@Component
public class Utils {
    private final MappaRepository mappaRepository;

    @Autowired
    public Utils(MappaRepository mappaRepository) {
        this.mappaRepository = mappaRepository;
    }

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

    public Partita initPartita() {
        Modalita modalita = Modalita.COMPLETA;
        Mappa mappa = mappaRepository.findById(1L).orElse(null);
        List<Giocatore> giocatori = new ArrayList<>();
        for(int i = 0; i < 5; i++) {
            giocatori.add(new Giocatore("Giocatore" + i));
        }

        return new Partita(mappa, giocatori);
    }
}
