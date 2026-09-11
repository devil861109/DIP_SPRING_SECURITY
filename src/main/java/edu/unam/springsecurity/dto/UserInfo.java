package edu.unam.springsecurity.dto;

import java.util.List;

public record UserInfo(String uid, String nombre, String apellido, String nombreCompleto,
                       String email, String dn, List<String> grupos) {}
