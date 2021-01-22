package it.engsoft.risiko.controllers;

import it.engsoft.risiko.exceptions.MossaIllegaleException;
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

    @PostMapping(path = "/gioco")
    public NuovoGiocoDAO nuovoGioco(@RequestBody NuovoGiocoDTO nuovoGiocoDTO, HttpSession httpSession) {
        Partita partita = partitaService.nuovoGioco(nuovoGiocoDTO);
        httpSession.setAttribute(partitaKey, partita);

        return new NuovoGiocoDAO(partita);
    }

    @PostMapping(path = "/rinforzi")
    public RinforzoDAO rinforzi(@RequestBody RinforzoDTO rinforzoDTO, HttpSession httpSession) {
        Partita partita = (Partita)httpSession.getAttribute(partitaKey);
        if (partita == null)
            throw new MossaIllegaleException("Mossa illegale: partita null");
        RinforzoDAO response = partitaService.rinforzo(rinforzoDTO, partita);
        if (response.isVittoria())  {
            httpSession.setAttribute(partitaKey, null);
        }
        return response;
    }

    @PostMapping(path = "/inizia-turno")
    public IniziaTurnoDAO iniziaTurno(HttpSession httpSession) {
        Partita partita = (Partita)httpSession.getAttribute(partitaKey);
        if (partita == null)
            throw new MossaIllegaleException("Mossa illegale: partita null");
        return partitaService.iniziaTurno(partita);
    }

    @PostMapping(path = "/tris")
    public int giocaTris(@RequestBody TrisDTO trisDTO, HttpSession httpSession) {
        Partita partita = (Partita)httpSession.getAttribute(partitaKey);
        if (partita == null)
            throw new MossaIllegaleException("Mossa illegale: partita null");
        return partitaService.giocaTris(trisDTO, partita);
    }

    @PostMapping(path = "/attacco")
    public void attacco(@RequestBody AttaccoDTO attaccoDTO, HttpSession httpSession) {
        Partita partita = (Partita)httpSession.getAttribute(partitaKey);
        if (partita == null)
            throw new MossaIllegaleException("Mossa illegale: partita null");
        partitaService.attacco(attaccoDTO, partita);
    }

    @PostMapping(path = "/difesa")
    public DifesaDAO difesa(@RequestBody DifesaDTO difesaDTO, HttpSession httpSession) {
        Partita partita = (Partita)httpSession.getAttribute(partitaKey);
        if (partita == null)
            throw new MossaIllegaleException("Mossa illegale: partita null");
        DifesaDAO difesa = partitaService.difesa(difesaDTO, partita);
        if(difesa.isObiettivoRaggiuntoAtt())
            httpSession.setAttribute(partitaKey, null);

        return difesa;
    }

    @PostMapping(path = "/spostamento")
    public boolean spostamento(@RequestBody SpostamentoDTO spostamentoDTO, HttpSession httpSession) {
        Partita partita = (Partita)httpSession.getAttribute(partitaKey);
        if (partita == null)
            throw new MossaIllegaleException("Mossa illegale: partita null");
        boolean obiettivoRaggiunto = partitaService.spostamentoStrategico(spostamentoDTO, partita);
        if (obiettivoRaggiunto)
            httpSession.setAttribute(partitaKey, null);
        return obiettivoRaggiunto;
    }

    @PostMapping(path = "/fine-turno")
    public CartaTerritorioDAO fineTurno(HttpSession httpSession) {
        Partita partita = (Partita)httpSession.getAttribute(partitaKey);
        if (partita == null)
            throw new MossaIllegaleException("Mossa illegale: partita null");
        return partitaService.fineTurno(partita);
    }
}
