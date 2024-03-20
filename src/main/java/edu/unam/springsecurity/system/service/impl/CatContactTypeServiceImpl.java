package edu.unam.springsecurity.system.service.impl;

import edu.unam.springsecurity.system.dto.CatContactTypeDTO;
import edu.unam.springsecurity.system.model.CatContactType;
import edu.unam.springsecurity.system.repository.CatContactTypeRepository;
import edu.unam.springsecurity.system.service.CatContactTypeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor // Ya no es necesario el autowired
public class CatContactTypeServiceImpl implements CatContactTypeService {
    private final CatContactTypeRepository catContactTypeRepository;

    @Override
    public List<CatContactTypeDTO> findAll() {
        log.info("Service - CatContactTypeServiceImpl.findAll");
        List<CatContactType> theList = catContactTypeRepository.findAll();
        return theList.stream().map(this::convertEntityToDTO).collect(Collectors.toList());
    }

    private CatContactTypeDTO convertEntityToDTO(CatContactType type) {
        return CatContactTypeDTO.builder()
                .cctContactTypeId(type.getCctContactTypeId())
                .cctName(type.getCctName())
                .cctStatus(type.getCctStatus())
                .build();
    }

}
