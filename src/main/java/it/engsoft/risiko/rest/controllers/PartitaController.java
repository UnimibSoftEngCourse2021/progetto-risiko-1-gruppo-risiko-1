package it.engsoft.risiko.rest.controllers;

import it.engsoft.risiko.exceptions.MossaIllegaleException;
import it.engsoft.risiko.service.PartitaService;
import it.engsoft.risiko.rest.dto.*;
import it.engsoft.risiko.data.model.Partita;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("api")
public class PartitaController {
    private final PartitaService partitaService;
    private final String partitaKey;
    private static final String exMessage = "Mossa illegale: partita null";

    @Autowired
    public PartitaController(PartitaService partitaService) {
        this.partitaService = partitaService;
        this.partitaKey = "partita";
    }

    @PostMapping(path = "/gioco")
    public NuovoGiocoResponse nuovoGioco(@RequestBody NuovoGiocoRequest nuovoGiocoRequest, HttpSession httpSession) {
        Partita partita = partitaService.nuovoGioco(nuovoGiocoRequest);
        httpSession.setAttribute(partitaKey, partita);

        return new NuovoGiocoResponse(partita);
    }

    @PostMapping(path = "/rinforzi")
    public RinforzoResponse rinforzi(@RequestBody RinforzoRequest rinforzoRequest, HttpSession httpSession) {
        Partita partita = (Partita)httpSession.getAttribute(partitaKey);
        if (partita == null)
            throw new MossaIllegaleException(exMessage);
        RinforzoResponse response = partitaService.rinforzo(rinforzoRequest, partita);
        if (response.isVittoria())  {
            httpSession.setAttribute(partitaKey, null);
        }
        return response;
    }

    @PostMapping(path = "/inizia-turno")
    public IniziaTurnoDTO iniziaTurno(HttpSession httpSession) {
        Partita partita = (Partita)httpSession.getAttribute(partitaKey);
        if (partita == null)
            throw new MossaIllegaleException(exMessage);
        return partitaService.iniziaTurno(partita);
    }

    @PostMapping(path = "/tris")
    public int giocaTris(@RequestBody TrisDTO trisDTO, HttpSession httpSession) {
        Partita partita = (Partita)httpSession.getAttribute(partitaKey);
        if (partita == null)
            throw new MossaIllegaleException(exMessage);
        return partitaService.giocaTris(trisDTO, partita);
    }

    @PostMapping(path = "/attacco")
    public void attacco(@RequestBody AttaccoDTO attaccoDTO, HttpSession httpSession) {
        Partita partita = (Partita)httpSession.getAttribute(partitaKey);
        if (partita == null)
            throw new MossaIllegaleException(exMessage);
        partitaService.attacco(attaccoDTO, partita);
    }

    @PostMapping(path = "/difesa")
    public DifesaResponse difesa(@RequestBody DifesaRequest difesaRequest, HttpSession httpSession) {
        Partita partita = (Partita)httpSession.getAttribute(partitaKey);
        if (partita == null)
            throw new MossaIllegaleException(exMessage);
        DifesaResponse difesa = partitaService.difesa(difesaRequest, partita);
        if(difesa.isObiettivoRaggiuntoAtt())
            httpSession.setAttribute(partitaKey, null);

        return difesa;
    }

    @PostMapping(path = "/spostamento")
    public boolean spostamento(@RequestBody SpostamentoDTO spostamentoDTO, HttpSession httpSession) {
        Partita partita = (Partita)httpSession.getAttribute(partitaKey);
        if (partita == null)
            throw new MossaIllegaleException(exMessage);
        boolean obiettivoRaggiunto = partitaService.spostamentoStrategico(spostamentoDTO, partita);
        if (obiettivoRaggiunto)
            httpSession.setAttribute(partitaKey, null);
        return obiettivoRaggiunto;
    }

    @PostMapping(path = "/fine-turno")
    public CartaTerritorioDTO fineTurno(HttpSession httpSession) {
        Partita partita = (Partita)httpSession.getAttribute(partitaKey);
        if (partita == null)
            throw new MossaIllegaleException(exMessage);
        return partitaService.fineTurno(partita);
    }
}
