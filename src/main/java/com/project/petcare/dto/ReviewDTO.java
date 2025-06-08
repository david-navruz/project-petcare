package com.project.petcare.dto;

public record ReviewDTO(
        Long id,
        int stars,
        String feedback,
        Long veterinarianId,
        String veterinarianName,
        Long petOwnerId,
        String petOwnerName,
        byte[] petOwnerImage,
        byte[] veterinarianImage
) {}

