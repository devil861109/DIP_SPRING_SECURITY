package edu.unam.springsecurity.system.service;

import edu.unam.springsecurity.system.dto.PetDTO;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PetStoreService {

    private final String URL = "https://petstore.swagger.io/v2/pet/";

    private RestTemplate rest() {
        return new RestTemplate();
    }

    // GET by status
    public PetDTO[] getByStatus(String status) {
        return rest().getForObject(URL + "findByStatus?status=" + status, PetDTO[].class);
    }

    // GET by ID
    public PetDTO getById(Long id) {
        return rest().getForObject(URL + id, PetDTO.class);
    }

    // CREATE
    public PetDTO create(PetDTO pet) {
        return rest().postForObject(URL, pet, PetDTO.class);
    }

    // UPDATE
    public PetDTO update(PetDTO pet) {
        HttpEntity<PetDTO> request = new HttpEntity<>(pet);
        ResponseEntity<PetDTO> response =
                rest().exchange(URL, HttpMethod.PUT, request, PetDTO.class);
        return response.getBody();
    }

    // DELETE
    public void delete(Long id) {
        rest().delete(URL + id);
    }
}
