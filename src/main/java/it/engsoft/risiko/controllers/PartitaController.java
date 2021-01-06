package it.engsoft.risiko.controllers;

import it.engsoft.risiko.service.PartitaService;
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

    @RequestMapping(method = RequestMethod.POST, path = "/gioco")
    public NuovoGiocoDAO nuovoGioco(@RequestBody NuovoGiocoDTO nuovoGiocoDTO) {
        // TODO: NuovoGiocoDAO
        return partitaService.nuovoGioco(nuovoGiocoDTO);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/rinforzi")
    public void rinforzi(@RequestBody RinforzoDTO rinforzoDTO) {
        partitaService.rinforzo(rinforzoDTO);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/inizia-turno")
    public IniziaTurnoDAO iniziaTurno() {
        return partitaService.iniziaTurno();
    }

    @RequestMapping(method = RequestMethod.POST, path = "/tris")
    public int giocaTris(@RequestBody TrisDTO trisDTO) {
        return partitaService.giocaTris(trisDTO);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/attacco")
    public void attacco(@RequestBody AttaccoDTO attaccoDTO) {

    }

    @RequestMapping(method = RequestMethod.POST, path = "/difesa")
    public DifesaDAO difesa(@RequestBody DifesaDTO difesaDTO) {
        // TODO: DifesaDAO
        return null;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/spostamento")
    public void spostamento(@RequestBody SpostamentoDTO spostamentoDTO) {
        partitaService.spostamentoStrategico(spostamentoDTO);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/fine-turno")
    public CartaTerritorioDAO fineTurno() {
        return partitaService.fineTurno();
    }
}
