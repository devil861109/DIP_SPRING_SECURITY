package edu.unam.springsecurity.system.dto;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PetDTO {

    private Long id;
    private String name;
    private Category category;
    private List<String> photoUrls;
    private List<Tag> tags;
    private String status;

    public static class Category {
        public Long id;
        public String name;
    }

    public static class Tag {
        public Long id;
        public String name;
    }
}
