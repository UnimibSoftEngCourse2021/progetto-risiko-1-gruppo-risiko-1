package it.engsoft.risiko.rest.controllers;

import it.engsoft.risiko.rest.dto.CompactMappaDTO;
import it.engsoft.risiko.rest.dto.NuovaMappaDTO;
import it.engsoft.risiko.service.MappaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Questa classe si occupa di gestire le richieste Http in ingresso relative alla gestione delle mappe.
 */
@RestController
@RequestMapping("api")
public class MappaController {
    private final MappaService mappaService;

    /**
     * Istanzia un nuovo MappaController facendo la dependency injection.
     * @param mappaService il service per la gestione delle mappe
     */
    @Autowired
    public MappaController(MappaService mappaService) {
        this.mappaService = mappaService;
    }

    /**
     * Ritorna l'elenco delle mappe esistenti con le informazioni essenziali.
     * @return l'elenco delle mappe
     */
    @GetMapping(path = "/mappe")
    public List<CompactMappaDTO> mappe() {
        return mappaService.mappe();
    }

    /**
     * Crea a salva una nuova mappa basata sulle informazioni ricevute in ingresso.
     * @param nuovaMappaDTO le informazioni della nuova mappa da salvare
     */
    @PostMapping(path = "/mappe")
    public void nuovaMappa(@RequestBody NuovaMappaDTO nuovaMappaDTO) {
        mappaService.nuovaMappa(nuovaMappaDTO);
    }
}
