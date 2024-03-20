package edu.unam.springsecurity.system.controller;

import edu.unam.springsecurity.system.dto.CatContactTypeDTO;
import edu.unam.springsecurity.system.service.CatContactTypeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("v1/contact-type/")
public class CatContactTypeController {
    private final CatContactTypeService catContactTypeService;

    @GetMapping("/get-contact-types")
    public ResponseEntity<?> getContactTypes(HttpServletRequest request) {
        List<CatContactTypeDTO> list = catContactTypeService.findAll();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
}
