package edu.unam.springsecurity.system.service;

import edu.unam.springsecurity.system.dto.PokemonDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PokemonService {

    private final String URL = "https://pokeapi.co/api/v2/pokemon/";

    public PokemonDTO getPokemon(String nameOrId) {
        RestTemplate rest = new RestTemplate();
        return rest.getForObject(URL + nameOrId.toLowerCase(), PokemonDTO.class);
    }
}
