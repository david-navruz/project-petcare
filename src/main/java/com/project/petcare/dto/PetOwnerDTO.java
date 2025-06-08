package com.project.petcare.dto;

public record PetOwnerDTO(
        Long petOwnerId,
        String firstName,
        String lastName,
        String email,
        String gender,
        String phoneNumber
) {}
