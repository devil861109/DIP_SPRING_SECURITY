package edu.unam.springsecurity.system.service;

import edu.unam.springsecurity.system.dto.CatContactTypeDTO;

import java.util.List;

public interface CatContactTypeService {
    List<CatContactTypeDTO> findAll();
}
