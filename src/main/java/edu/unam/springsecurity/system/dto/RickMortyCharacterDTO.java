package edu.unam.springsecurity.system.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Builder
@Getter
@Setter
@ToString
public class RickMortyCharacterDTO {
    private int id;
    private String name;
    private String status;
    private String species;
    private String type;
    private String gender;

    private Origin origin;
    private Location location;

    private String image;
    private List<String> episode;

    public static class Origin {
        public String name;
        public String url;
    }

    public static class Location {
        public String name;
        public String url;
    }
}
