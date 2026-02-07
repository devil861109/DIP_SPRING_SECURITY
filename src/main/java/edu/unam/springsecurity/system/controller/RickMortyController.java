package edu.unam.springsecurity.system.controller;

import edu.unam.springsecurity.system.dto.RickMortyCharacterDTO;
import edu.unam.springsecurity.system.service.RickMortyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RickMortyController {

    @Autowired
    private RickMortyService service;

    @GetMapping("/rickmorty")
    public String mostrarPersonaje(
            @RequestParam(required = false) String personaje,
            Model model) {

        if (personaje != null && !personaje.isEmpty()) {
            try {
                RickMortyCharacterDTO data = service.getCharacter(personaje);
                model.addAttribute("personaje", data);
            } catch (Exception e) {
                model.addAttribute("error", "Personaje no encontrado");
            }
        }

        return "rickmorty";
    }
}
