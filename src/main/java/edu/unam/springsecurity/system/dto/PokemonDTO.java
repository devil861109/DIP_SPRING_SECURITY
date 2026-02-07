package edu.unam.springsecurity.system.dto;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@ToString
public class PokemonDTO {
    private int id;
    private String name;
    private int height;
    private int weight;

    private Sprites sprites;

    private List<TypeSlot> types;
    private List<AbilitySlot> abilities;
    private List<MoveSlot> moves;
    private List<StatSlot> stats;

    // ---------------- SPRITES SIMPLIFICADOS ----------------
    public static class Sprites {
        public String front_default;
        public String front_shiny;
    }

    // ---------------- TIPOS ----------------
    public static class TypeSlot {
        public Type type;

        public static class Type {
            public String name;
            public String url;
        }
    }

    // ---------------- HABILIDADES ----------------
    public static class AbilitySlot {
        public Ability ability;
        public boolean is_hidden;
        public int slot;

        public static class Ability {
            public String name;
            public String url;
        }
    }

    // ---------------- MOVIMIENTOS ----------------
    public static class MoveSlot {
        public Move move;
        public List<VersionGroupDetail> version_group_details;

        public static class Move {
            public String name;
            public String url;
        }

        public static class VersionGroupDetail {
            public int level_learned_at;
            public MoveLearnMethod move_learn_method;
            public VersionGroup version_group;

            public static class MoveLearnMethod {
                public String name;
                public String url;
            }

            public static class VersionGroup {
                public String name;
                public String url;
            }
        }
    }

    // ---------------- ESTADÍSTICAS BASE ----------------
    public static class StatSlot {
        public int base_stat;
        public int effort;
        public Stat stat;

        public static class Stat {
            public String name;
            public String url;
        }
    }
}
