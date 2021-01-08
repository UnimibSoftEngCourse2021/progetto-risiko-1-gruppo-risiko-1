package it.engsoft.risiko.controllers;

import it.engsoft.risiko.service.PartitaService;
import it.engsoft.risiko.dto.*;
import it.engsoft.risiko.dao.*;
import it.engsoft.risiko.model.Partita;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("api")
public class PartitaController {
    private final PartitaService partitaService;
    private final String partitaKey;

    @Autowired
    public PartitaController(PartitaService partitaService) {
        this.partitaService = partitaService;
        this.partitaKey = "partita";
    }

    @RequestMapping(method = RequestMethod.POST, path = "/gioco")
    public NuovoGiocoDAO nuovoGioco(@RequestBody NuovoGiocoDTO nuovoGiocoDTO, HttpSession httpSession) {
        Partita partita = partitaService.nuovoGioco(nuovoGiocoDTO);
        httpSession.setAttribute(partitaKey, partita);

        return new NuovoGiocoDAO(partita);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/rinforzi")
    public boolean rinforzi(@RequestBody RinforzoDTO rinforzoDTO, HttpSession httpSession) {
        Partita partita = (Partita)httpSession.getAttribute(partitaKey);
        return partitaService.rinforzo(rinforzoDTO, partita);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/inizia-turno")
    public IniziaTurnoDAO iniziaTurno(HttpSession httpSession) {
        Partita partita = (Partita)httpSession.getAttribute(partitaKey);
        return partitaService.iniziaTurno(partita);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/tris")
    public int giocaTris(@RequestBody TrisDTO trisDTO, HttpSession httpSession) {
        Partita partita = (Partita)httpSession.getAttribute(partitaKey);
        return partitaService.giocaTris(trisDTO, partita);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/attacco")
    public void attacco(@RequestBody AttaccoDTO attaccoDTO, HttpSession httpSession) {
        Partita partita = (Partita)httpSession.getAttribute(partitaKey);
        partitaService.attacco(attaccoDTO, partita);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/difesa")
    public DifesaDAO difesa(@RequestBody DifesaDTO difesaDTO, HttpSession httpSession) {
        Partita partita = (Partita)httpSession.getAttribute(partitaKey);
        return partitaService.difesa(difesaDTO, partita);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/spostamento")
    public void spostamento(@RequestBody SpostamentoDTO spostamentoDTO, HttpSession httpSession) {
        Partita partita = (Partita)httpSession.getAttribute(partitaKey);
        partitaService.spostamentoStrategico(spostamentoDTO, partita);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/fine-turno")
    public CartaTerritorioDAO fineTurno(HttpSession httpSession) {
        Partita partita = (Partita)httpSession.getAttribute(partitaKey);
        return partitaService.fineTurno(partita);
    }
}
