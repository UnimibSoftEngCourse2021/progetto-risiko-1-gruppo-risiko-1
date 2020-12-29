package it.engsoft.risiko.controllers;

import it.engsoft.risiko.dao.MappaDAO;
import it.engsoft.risiko.model.Mappa;
import it.engsoft.risiko.repository.MappaRepository;
import it.engsoft.risiko.service.PartitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;

@RestController
@RequestMapping("api")
@Scope(WebApplicationContext.SCOPE_SESSION)
public class ControllerProva {
    private final PartitaService partitaService;
    private final MappaRepository mappaRepo;

    @Autowired
    public ControllerProva(PartitaService partitaService, MappaRepository mappaRepository) {
        this.partitaService = partitaService;
        this.mappaRepo = mappaRepository;
    }

    @RequestMapping(method = RequestMethod.GET, path = "prova/{id}")
    public MappaDAO getUsername(@PathVariable("id") Long id) {
        Mappa mappa = mappaRepo.findById(id);
        return new MappaDAO(mappa);
    }
}
