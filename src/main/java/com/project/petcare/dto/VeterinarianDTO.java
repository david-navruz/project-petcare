package com.project.petcare.dto;

public record VeterinarianDTO(
        Long veterinarianId,
        String firstName,
        String lastName,
        String email,
        String gender,
        String phoneNumber,
        String specialization
) {}

