package com.project.petcare.dto;

import com.project.petcare.model.AppointmentStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AppointmentDTO {
    private Long id;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private LocalDate createdAt;
    private String reason;
    private AppointmentStatus status;
    private String appointmentNo;
    private PetOwnerDTO patient;
    private VeterinarianDTO veterinarian;
    private List<PetDTO> pets;
}
