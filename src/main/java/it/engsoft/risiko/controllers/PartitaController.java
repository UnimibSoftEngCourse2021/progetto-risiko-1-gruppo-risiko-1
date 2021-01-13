package it.engsoft.risiko.controllers;

import it.engsoft.risiko.exceptions.MossaIllegaleException;
import it.engsoft.risiko.service.PartitaService;
import it.engsoft.risiko.dto.*;
import it.engsoft.risiko.dao.*;
import it.engsoft.risiko.model.Partita;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;

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

    @PostMapping(path = "/gioco")
    public NuovoGiocoDAO nuovoGioco(@RequestBody NuovoGiocoDTO nuovoGiocoDTO, HttpSession httpSession) {
        Partita partita = partitaService.nuovoGioco(nuovoGiocoDTO);
        httpSession.setAttribute(partitaKey, partita);

        return new NuovoGiocoDAO(partita);
    }

    @PostMapping(path = "/rinforzi")
    public Map<String, Object> rinforzi(@RequestBody RinforzoDTO rinforzoDTO, HttpSession httpSession) {
        Partita partita = (Partita)httpSession.getAttribute(partitaKey);
        if (partita == null)
            throw new MossaIllegaleException();
        return partitaService.rinforzo(rinforzoDTO, partita);
    }

    @PostMapping(path = "/inizia-turno")
    public IniziaTurnoDAO iniziaTurno(HttpSession httpSession) {
        Partita partita = (Partita)httpSession.getAttribute(partitaKey);
        if (partita == null)
            throw new MossaIllegaleException();
        return partitaService.iniziaTurno(partita);
    }

    @PostMapping(path = "/tris")
    public int giocaTris(@RequestBody TrisDTO trisDTO, HttpSession httpSession) {
        Partita partita = (Partita)httpSession.getAttribute(partitaKey);
        if (partita == null)
            throw new MossaIllegaleException();
        return partitaService.giocaTris(trisDTO, partita);
    }

    @PostMapping(path = "/attacco")
    public void attacco(@RequestBody AttaccoDTO attaccoDTO, HttpSession httpSession) {
        Partita partita = (Partita)httpSession.getAttribute(partitaKey);
        if (partita == null)
            throw new MossaIllegaleException();
        partitaService.attacco(attaccoDTO, partita);
    }

    @PostMapping(path = "/difesa")
    public DifesaDAO difesa(@RequestBody DifesaDTO difesaDTO, HttpSession httpSession) {
        Partita partita = (Partita)httpSession.getAttribute(partitaKey);
        if (partita == null)
            throw new MossaIllegaleException();
        DifesaDAO difesa = partitaService.difesa(difesaDTO, partita);
        if(difesa.isObiettivoRaggiuntoAtt())
            httpSession.setAttribute(partitaKey, null);

        return difesa;
    }

    @PostMapping(path = "/spostamento")
    public void spostamento(@RequestBody SpostamentoDTO spostamentoDTO, HttpSession httpSession) {
        Partita partita = (Partita)httpSession.getAttribute(partitaKey);
        if (partita == null)
            throw new MossaIllegaleException();
        partitaService.spostamentoStrategico(spostamentoDTO, partita);
    }

    @PostMapping(path = "/fine-turno")
    public CartaTerritorioDAO fineTurno(HttpSession httpSession) {
        Partita partita = (Partita)httpSession.getAttribute(partitaKey);
        if (partita == null)
            throw new MossaIllegaleException();
        return partitaService.fineTurno(partita);
    }
}
