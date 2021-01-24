package it.engsoft.risiko.service;

import it.engsoft.risiko.data.creators.MappaBuilder;
import it.engsoft.risiko.rest.DTO.*;
import it.engsoft.risiko.exceptions.NotFoundException;
import it.engsoft.risiko.data.model.*;
import it.engsoft.risiko.exceptions.DatiErratiException;
import it.engsoft.risiko.data.repository.MappaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MappaService {
    private final MappaRepository mappaRepository;

    @Autowired
    public MappaService(MappaRepository mappaRepository) {
        this.mappaRepository = mappaRepository;
    }

    /**
     * Restituisce l'elenco delle mappe salvate nel database.
     *
     * @return dati riassuntivi di tutte le mappe
     */
    public List<CompactMappaDTO> mappe() {
        List<Mappa> mappe = mappaRepository.findAll();

        return mappe.stream()
                .map(CompactMappaDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Restituisce la mappa avente l'id specificato.
     *
     * @param mappaId: l'id della mappa da restituire
     * @return dati della mappa da visualizzare a front-end
     */
    public MappaDTO mappa(Long mappaId) {
        Optional<Mappa> optMappa = mappaRepository.findById(mappaId);
        if (optMappa.isEmpty())
            throw new NotFoundException("Mappa non trovata");

        return new MappaDTO(optMappa.get());
    }

    /**
     * Recupera dal database la mappa indicata dall'id e la compatta secondo la modalitá indicata.
     * @param mappaId: id della mappa da recuperare
     * @param mod: modalitá secondo la quale va compattata la mappa
     * @return mappa rischiesta compattata
     */
    public Mappa getMappa(Long mappaId, Modalita mod) {
        Optional<Mappa> optMappa = mappaRepository.findById(mappaId);
        if (optMappa.isEmpty())
            throw new DatiErratiException("Dati errati: la mappa e' vuota");
        Mappa mappa = optMappa.get();

        mappa.compatta(mod);

        return mappa;
    }



    /**
     * Controlla che i dati ricevuti siano corretti, crea una nuova mappa e la salva sul database.
     *
     * @param nuovaMappaDTO: oggetto contenente i dati con cui creare la nuova mappa
     */
    public void nuovaMappa(NuovaMappaDTO nuovaMappaDTO) {
        MappaBuilder builder = new MappaBuilder(nuovaMappaDTO.getNome(), nuovaMappaDTO.getDescrizione(),
                nuovaMappaDTO.getNumMinGiocatori(), nuovaMappaDTO.getNumMaxGiocatori());
        for (NuovoContinenteDTO continente : nuovaMappaDTO.getContinenti()) {
            builder.addContinente(continente.getNome(), continente.getArmateBonus());
            for (NuovoStatoDTO stato: continente.getStati()) {
                builder.addStato(stato.getNome(), continente.getNome());
            }
        }

        nuovaMappaDTO.getContinenti().forEach(c -> c.getStati().forEach(stato ->
            stato.getConfinanti().forEach(nomeConf -> builder.addConfine(stato.getNome(), nomeConf))
        ));

        Mappa nuovaMappa = builder.build();
        mappaRepository.save(nuovaMappa);
    }

}
