package com.project.petcare.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public record UserDTO(
        Long id,
        String firstName,
        String lastName,
        String gender,
        String phoneNumber,
        String email,
        String userType,
        boolean isEnabled,
        String specialization,
        LocalDate createdAt,
        List<AppointmentDTO> appointments,
        List<ReviewDTO> reviews,
        long photoId,
        byte[] photo,
        double averageRating,
        Set<String> roles,
        Long totalReviewers
) {}
