package com.project.petcare.dto;

import com.project.petcare.model.AppointmentStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record AppointmentDTO(
        Long id,
        LocalDate appointmentDate,
        LocalTime appointmentTime,
        LocalDate createdAt,
        String reason,
        AppointmentStatus status,
        String appointmentNo,
        PetOwnerDTO patient,
        VeterinarianDTO veterinarian,
        List<PetDTO> pets
) {}
