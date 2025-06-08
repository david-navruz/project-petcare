package com.project.petcare.dto;

public record PetDTO(
        Long id,
        String name,
        String type,
        String color,
        String breed,
        int age
) {}

