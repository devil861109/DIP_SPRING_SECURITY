package edu.unam.springsecurity.dto;

public record NewUser(String uid, String nombre, String apellido,
                           String email, String password, String grupo) {}
