package it.engsoft.risiko.rest.controllers;

import it.engsoft.risiko.rest.DTO.CompactMappaDTO;
import it.engsoft.risiko.rest.DTO.MappaDTO;
import it.engsoft.risiko.rest.DTO.NuovaMappaDTO;
import it.engsoft.risiko.service.MappaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

@RestController
@RequestMapping("api")
@Scope(WebApplicationContext.SCOPE_SESSION)
public class MappaController {
    private final MappaService mappaService;

    @Autowired
    public MappaController(MappaService mappaService) {
        this.mappaService = mappaService;
    }

    @GetMapping(path = "/mappe")
    public List<CompactMappaDTO> mappe() {
        return mappaService.mappe();
    }

    @GetMapping(path = "/mappe/{id}")
    public MappaDTO mappa(@PathVariable("id") Long mappaId) {
        return mappaService.mappa(mappaId);
    }

    @PostMapping(path = "/mappe")
    public void nuovaMappa(@RequestBody NuovaMappaDTO nuovaMappaDTO) {
        mappaService.nuovaMappa(nuovaMappaDTO);
    }
}
