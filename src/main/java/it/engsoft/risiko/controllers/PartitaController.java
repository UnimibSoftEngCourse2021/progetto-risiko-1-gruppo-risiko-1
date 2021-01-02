package it.engsoft.risiko.controllers;

import it.engsoft.risiko.service.*;
import it.engsoft.risiko.dto.*;
import it.engsoft.risiko.dao.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

@RestController
@RequestMapping("api")
@Scope(WebApplicationContext.SCOPE_SESSION)
public class PartitaController {
    private final PartitaService partitaService;

    @Autowired
    public PartitaController(PartitaService partitaService) {
        this.partitaService = partitaService;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/mappe")
    public List<MappaDAO> mappe() {
        return partitaService.mappe();
    }

    @RequestMapping(method = RequestMethod.GET, path = "/mappa/{id}")
    public MappaDAO getUsername(@PathVariable("id") Long id) {
        return partitaService.mappa(id);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/nuovoGioco")
    public NuovoGiocoDAO nuovoGioco(@RequestBody NuovoGiocoDTO nuovoGiocoDTO) {
        // TODO: NuovoGiocoDAO
        return partitaService.nuovoGioco(nuovoGiocoDTO);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/rinforzi")
    public void rinforzi(@RequestBody RinforzoDTO rinforzoDTO) {
        partitaService.rinforzo(rinforzoDTO);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/inizia_turno")
    public IniziaTurnoDAO iniziaTurno() {
        return partitaService.iniziaTurno();
    }

    @RequestMapping(method = RequestMethod.POST, path = "/tris")
    public int giocaTris(@RequestBody TrisDTO TrisDTO) {
        return partitaService.giocaTris(requestTrisDTO);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/attacco")
    public void attacco(@RequestBody AttaccoDTO attaccoDTO) {

    }

    @RequestMapping(method = RequestMethod.POST, path = "/difesa")
    public DifesaDAO difesa(@RequestBody DifesaDTO difesaDTO) {
        // TODO: DifesaDAO
    }

    @RequestMapping(method = RequestMethod.POST, path = "/spostamento")
    public void spostamento(@RequestBody SpostamentoDTO spostamentoDTO) {
        partitaService.spostamentoStrategico(spostamentoDTO);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/fine_turno")
    public CartaTerritorioDAO fineTurno() {
        // TODO: CartaTerritorioDAO
        return partitaService.fineTurno();
    }
}
