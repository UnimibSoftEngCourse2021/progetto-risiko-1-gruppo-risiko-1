package it.engsoft.risiko.controllers;

import it.engsoft.risiko.service.PartitaService;
import it.engsoft.risiko.dto.*;
import it.engsoft.risiko.dao.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api")
public class PartitaController {
    private final PartitaService partitaService;

    @Autowired
    public PartitaController(PartitaService partitaService) {
        this.partitaService = partitaService;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/gioco")
    public NuovoGiocoDAO nuovoGioco(@RequestBody NuovoGiocoDTO nuovoGiocoDTO) {
        return partitaService.nuovoGioco(nuovoGiocoDTO);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/rinforzi")
    public boolean rinforzi(@RequestBody RinforzoDTO rinforzoDTO) {
        return partitaService.rinforzo(rinforzoDTO);
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
        partitaService.attacco(attaccoDTO);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/difesa")
    public DifesaDAO difesa(@RequestBody DifesaDTO difesaDTO) {
        return partitaService.difesa(difesaDTO);
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
