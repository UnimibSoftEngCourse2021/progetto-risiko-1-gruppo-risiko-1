package it.engsoft.risiko.controllers;

import org.springframework.web.bind.annotation.*;

@RestController
public class ControllerProva {

    @RequestMapping(method = RequestMethod.POST, path = "prova/{id}")
    public String getUsername(@PathVariable int id, @RequestParam String parametroProva) {
        return "ID = " + id + ", paramProva = " + parametroProva;
    }

}
