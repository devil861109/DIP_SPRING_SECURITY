package edu.unam.springsecurity.system.controller;

import edu.unam.springsecurity.system.dto.PetDTO;
import edu.unam.springsecurity.system.service.PetStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/pets")
public class PetStoreController {

    @Autowired
    private PetStoreService service;

    // LISTAR
    @GetMapping
    public String listar(@RequestParam(defaultValue = "available") String status, Model model) {
        model.addAttribute("pets", service.getByStatus(status));
        model.addAttribute("status", status);
        return "pets-list";
    }

    // FORM CREAR
    @GetMapping("/crear")
    public String crearForm(Model model) {
        model.addAttribute("pet", new PetDTO());
        return "pets-crear";
    }

    // GUARDAR
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute PetDTO pet) {
        service.create(pet);
        return "redirect:/pets";
    }

    // FORM EDITAR
    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Long id, Model model) {
        model.addAttribute("pet", service.getById(id));
        return "pets-editar";
    }

    // ACTUALIZAR
    @PostMapping("/actualizar")
    public String actualizar(@ModelAttribute PetDTO pet) {
        service.update(pet);
        return "redirect:/pets";
    }

    // ELIMINAR
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/pets";
    }
}

