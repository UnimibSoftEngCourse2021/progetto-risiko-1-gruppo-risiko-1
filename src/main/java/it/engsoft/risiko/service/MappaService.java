package it.engsoft.risiko.service;

import it.engsoft.risiko.dao.*;
import it.engsoft.risiko.dto.*;
import it.engsoft.risiko.model.*;
import it.engsoft.risiko.exceptions.DatiErratiException;
import it.engsoft.risiko.repository.MappaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

// TODO: MappaService
@Service
public class MappaService {
    private final MappaRepository mappaRepository;

    @Autowired
    public MappaService(MappaRepository mappaRepository) {
        this.mappaRepository = mappaRepository;
    }

    public List<CompactMappaDAO> mappe() {
        List<Mappa> mappe = mappaRepository.findAll();

        List<CompactMappaDAO> compactMappe = mappe.stream()
                .map(CompactMappaDAO::new)
                .collect(Collectors.toList());

        return compactMappe;
    }

    public MappaDAO mappa(Long mappaId) {
        Optional<Mappa> optMappa = mappaRepository.findById(mappaId);
        if (optMappa.isEmpty())
            throw new DatiErratiException();

        return new MappaDAO(optMappa.get());
    }

    public void nuovaMappa(NuovaMappaDTO nuovaMappaDTO) {
        List<Continente> continenti = new ArrayList<>();
        Map<String, Stato> nomi_stati = new HashMap<>();

        // Crea la mappa
        Mappa mappa = new Mappa(nuovaMappaDTO.getNome(),
                nuovaMappaDTO.getDescrizione(),
                nuovaMappaDTO.getNumMinGiocatori(),
                nuovaMappaDTO.getNumMaxGiocatori());

        // Crea i continenti
        for (NuovoContinenteDTO nuovoContinente : nuovaMappaDTO.getContinenti()) {
            Continente continente = new Continente(nuovoContinente.getNome(), nuovoContinente.getArmateBonus());
            continente.setMappa(mappa);
            mappa.aggiungiContinente(continente);

            // Crea gli stati e li inserisce nel loro continente
            for (NuovoStatoDTO nuovoStato : nuovoContinente.getStati()) {
                Stato stato = new Stato(nuovoStato.getNome(), continente);
                continente.aggiungiStato(stato);

                nomi_stati.put(stato.getNome(), stato);
            }
        }

        // Aggiunge i confinanti agli stati
        for (NuovoContinenteDTO nuovoContinente : nuovaMappaDTO.getContinenti()) {
            for (NuovoStatoDTO nuovoStato : nuovoContinente.getStati()) {
                for (String nomeStato : nuovoStato.getConfinanti()) {
                    nomi_stati.get(nuovoStato.getNome()).aggiungiConfinante(nomi_stati.get(nomeStato));
                }
            }
        }

        // Salva la mappa sul DB
        mappaRepository.save(mappa);
    }


}
