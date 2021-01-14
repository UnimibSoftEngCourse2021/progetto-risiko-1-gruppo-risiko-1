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

@Service
public class MappaService {
    private final MappaRepository mappaRepository;

    @Autowired
    public MappaService(MappaRepository mappaRepository) {
        this.mappaRepository = mappaRepository;
    }

    /**
     * Restituisce l'elenco delle mappe salvate nel database.
     * @return dati riassuntivi di tutte le mappe
     */
    public List<CompactMappaDAO> mappe() {
        List<Mappa> mappe = mappaRepository.findAll();

        List<CompactMappaDAO> compactMappe = mappe.stream()
                .map(CompactMappaDAO::new)
                .collect(Collectors.toList());

        return compactMappe;
    }

    /**
     * Restituisce la mappa avente l'id specificato.
     * @param mappaId: l'id della mappa da restituire
     * @return dati della mappa da visualizzare a front-end
     */
    public MappaDAO mappa(Long mappaId) {
        Optional<Mappa> optMappa = mappaRepository.findById(mappaId);
        if (optMappa.isEmpty())
            throw new DatiErratiException();

        return new MappaDAO(optMappa.get());
    }

    /**
     * Controlla che i dati ricevuti siano corretti, crea una nuova mappa e la salva sul database.
     * @param nuovaMappaDTO: oggetto contenente i dati con cui creare la nuova mappa
     */
    public void nuovaMappa(NuovaMappaDTO nuovaMappaDTO) {
        if(!nomiUnivoci(nuovaMappaDTO))
            throw new DatiErratiException();

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

        aggiungiConfinanti(nomi_stati, nuovaMappaDTO);
        checkConfinanti(nomi_stati);

        mappaRepository.save(mappa);
    }

    /**
     * Verifica che i nomi dei continenti e degli stati siano univoci.
     * @param nuovaMappaDTO: la mappa contenente i dati da controllare
     * @return true se i nomi sono univoci, false altrimenti.
     */
    private boolean nomiUnivoci(NuovaMappaDTO nuovaMappaDTO) {
        List<String> nomi = new ArrayList<>();

        for (NuovoContinenteDTO continente : nuovaMappaDTO.getContinenti()) {
            if(nomi.contains(continente.getNome()))
                return false;

            nomi.add(continente.getNome());

            for (NuovoStatoDTO stato : continente.getStati()) {
                if(nomi.contains(stato.getNome()))
                    return false;

                nomi.add(stato.getNome());
            }
        }

        return true;
    }

    /**
     * Aggiunge ad ogni stato i riferimenti agli stati a lui confinanti.
     * @param nomi_stati: Map contenente i nomi degli stati e gli stati stessi
     * @param nuovaMappaDTO: la mappa contenente l'elenco di confinanti per ogni stato
     */
    private void aggiungiConfinanti(Map<String, Stato> nomi_stati, NuovaMappaDTO nuovaMappaDTO) {
        for (NuovoContinenteDTO nuovoContinente : nuovaMappaDTO.getContinenti()) {
            for (NuovoStatoDTO nuovoStato : nuovoContinente.getStati()) {
                for (String confinante : nuovoStato.getConfinanti()) {
                    nomi_stati.get(nuovoStato.getNome()).aggiungiConfinante(nomi_stati.get(confinante));
                }
            }
        }
    }

    /**
     * Verifica che la relazione 'confinante' sia presente in entrambi i sensi.
     * @param nomi_stati: Map contenente i nomi degli stati e gli stati stessi
     */
    private void checkConfinanti(Map<String, Stato> nomi_stati) {
        for (Stato stato : nomi_stati.values()) {
            for(Stato confinante : stato.getConfinanti()) {
                if(!confinante.getConfinanti().contains(stato))
                    throw new DatiErratiException();
            }
        }
    }
}
