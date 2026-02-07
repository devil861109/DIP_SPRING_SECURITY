package edu.unam.springsecurity.system.service;

import edu.unam.springsecurity.system.dto.RickMortyCharacterDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class RickMortyService {

    private final String URL = "https://rickandmortyapi.com/api/character/";

    public RickMortyCharacterDTO getCharacter(String idOrName) {
        RestTemplate rest = new RestTemplate();

        // Si es número, consulta por ID
        if (idOrName.matches("\\d+")) {
            return rest.getForObject(URL + idOrName, RickMortyCharacterDTO.class);
        }

        // Si es nombre, consulta por búsqueda
        String searchUrl = "https://rickandmortyapi.com/api/character/?name=" + idOrName;
        RickMortySearchResponse response =
                rest.getForObject(searchUrl, RickMortySearchResponse.class);

        if (response != null && response.results != null && !response.results.isEmpty()) {
            return response.results.get(0);
        }

        throw new RuntimeException("Personaje no encontrado");
    }

    // DTO para búsqueda
    public static class RickMortySearchResponse {
        public List<RickMortyCharacterDTO> results;
    }
}

