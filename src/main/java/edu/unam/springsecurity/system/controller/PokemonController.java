package edu.unam.springsecurity.system.controller;

import edu.unam.springsecurity.system.dto.PokemonDTO;
import edu.unam.springsecurity.system.service.PokemonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PokemonController {

    @Autowired
    private PokemonService pokemonService;

    @GetMapping("/pokemon")
    public String mostrarPokemon(
            @RequestParam(required = false) String nombre,
            Model model) {

        if (nombre != null && !nombre.isEmpty()) {
            try {
                PokemonDTO pokemon = pokemonService.getPokemon(nombre);
                model.addAttribute("pokemon", pokemon);
            } catch (Exception e) {
                model.addAttribute("error", "Pokémon no encontrado");
            }
        }
        return "pokemon";
    }
}
