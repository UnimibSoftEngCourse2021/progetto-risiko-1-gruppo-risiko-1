package it.engsoft.risiko.controllers;

import it.engsoft.risiko.service.PartitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api")
public class ControllerProva {
    private final PartitaService partitaService;

    @Autowired
    public ControllerProva(PartitaService partitaService) {
        this.partitaService = partitaService;
    }

    @RequestMapping(method = RequestMethod.POST, path = "prova/{id}")
    public String getUsername(@PathVariable int id, @RequestParam String parametroProva) {
        return "ID = " + id + ", paramProva = " + parametroProva;
    }


}
