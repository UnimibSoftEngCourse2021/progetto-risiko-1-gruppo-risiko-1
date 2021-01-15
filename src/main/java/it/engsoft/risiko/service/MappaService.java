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
     *
     * @return dati riassuntivi di tutte le mappe
     */
    public List<CompactMappaDAO> mappe() {
        List<Mappa> mappe = mappaRepository.findAll();

        return mappe.stream()
                .map(CompactMappaDAO::new)
                .collect(Collectors.toList());
    }

    /**
     * Restituisce la mappa avente l'id specificato.
     *
     * @param mappaId: l'id della mappa da restituire
     * @return dati della mappa da visualizzare a front-end
     */
    public MappaDAO mappa(Long mappaId) {
        Optional<Mappa> optMappa = mappaRepository.findById(mappaId);
        if (optMappa.isEmpty())
            throw new DatiErratiException("Dati errati: la mappa e' vuota");

        return new MappaDAO(optMappa.get());
    }

    /**
     * Recupera dal database la mappa indicata dall'id e la compatta secondo la modalitá indicata.
     * @param mappaId: id della mappa da recuperare
     * @param mod: modalitá secondo la quale va compattata la mappa
     * @return mappa rischiesta compattata
     */
    public Mappa getMappa(Long mappaId, String mod) {
        Optional<Mappa> optMappa = mappaRepository.findById(mappaId);
        if (optMappa.isEmpty())
            throw new DatiErratiException("Dati errati: la mappa e' vuota");
        Mappa mappa = optMappa.get();

        compattaMappa(mappa, mod);

        return mappa;
    }

    /**
     * Riceve una mappa e la compatta secondo la modalitá indicata.
     * @param mappa: la mappa da compattare
     * @param mod: modalitá secondo la quale va compattata la mappa
     */
    private void compattaMappa(Mappa mappa, String mod) {
        int n = 0;

        if (mod.equals("COMPLETA"))
            return;
        else if (mod.equals("RIDOTTA"))
            n = 3;
        else // VELOCE
            n = 2;

        Stato statoCompattato = null;
        List<Stato> statiDaRimuovere = new ArrayList<>();

        for (Continente continente : mappa.getContinenti()) {
            for (int i = 0; i < continente.getStati().size(); i++) {
                Stato stato = continente.getStati().get(i);

                if (i % n == 0) {
                    statoCompattato = stato;
                }
                else if (i % n == 1) {
                    mergeStati(statoCompattato, stato);
                    statiDaRimuovere.add(stato);
                }
            }

            continente.rimuoviStati(statiDaRimuovere);
        }
    }

    /**
     * Unisce due stati. Id, nome e armate rimangono quelli del primo stato.
     * @param a
     * @param b
     */
    private void mergeStati(Stato a, Stato b) {
        a.rimuoviConfinante(b);
        b.rimuoviConfinante(a);
        a.aggiungiConfinanti(b.getConfinanti());

        for (Stato stato : b.getConfinanti()) {
            stato.rimuoviConfinante(b);
            stato.aggiungiConfinante(a);
        }
    }

    /**
     * Controlla che i dati ricevuti siano corretti, crea una nuova mappa e la salva sul database.
     *
     * @param nuovaMappaDTO: oggetto contenente i dati con cui creare la nuova mappa
     */
    public void nuovaMappa(NuovaMappaDTO nuovaMappaDTO) {
        if (!nomiUnivoci(nuovaMappaDTO))
            throw new DatiErratiException("Dati errati: il nome della mappa non e' univoco");

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
        grafoConnesso(new ArrayList<>(nomi_stati.values()));

        mappaRepository.save(mappa);
    }

    /**
     * Verifica che i nomi dei continenti e degli stati siano univoci.
     *
     * @param nuovaMappaDTO: la mappa contenente i dati da controllare
     * @return true se i nomi sono univoci, false altrimenti.
     */
    private boolean nomiUnivoci(NuovaMappaDTO nuovaMappaDTO) {
        List<String> nomi = new ArrayList<>();

        for (NuovoContinenteDTO continente : nuovaMappaDTO.getContinenti()) {
            if (nomi.contains(continente.getNome()))
                return false;

            nomi.add(continente.getNome());

            for (NuovoStatoDTO stato : continente.getStati()) {
                if (nomi.contains(stato.getNome()))
                    return false;

                nomi.add(stato.getNome());
            }
        }

        return true;
    }

    /**
     * Aggiunge ad ogni stato i riferimenti agli stati a lui confinanti.
     *
     * @param nomi_stati:    Map contenente i nomi degli stati e gli stati stessi
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
     *
     * @param nomi_stati: Map contenente i nomi degli stati e gli stati stessi
     */
    private void checkConfinanti(Map<String, Stato> nomi_stati) {
        for (Stato stato : nomi_stati.values()) {
            for (Stato confinante : stato.getConfinanti()) {
                if(confinante.getConfinanti().stream().noneMatch(c -> c.getNome().equals(stato.getNome())))
                    throw new DatiErratiException("Dati errati: gli stati non sono confinanti");

            }
        }
    }

    /**
     * Controlla che ogni nodo sia raggiungibile
     * @param grafo: lista contenente gli stati
     */
    private void grafoConnesso(List<Stato> grafo) {
        List<Stato> visitati = new ArrayList<>();
        LinkedList<Stato> queue = new LinkedList<>();

        visitati.add(grafo.get(0));
        queue.offer(grafo.get(0));

        while (!queue.isEmpty()) {
            Stato statoCorrente = queue.poll();

            for(Stato confinante : statoCorrente.getConfinanti()) {
                if(visitati.stream().noneMatch(v -> v.getNome().equals(confinante.getNome()))) {
                    visitati.add(confinante);
                    queue.offer(confinante);
                }
            }
        }

        if(visitati.size() != grafo.size())
            throw new DatiErratiException("");
    }
}
