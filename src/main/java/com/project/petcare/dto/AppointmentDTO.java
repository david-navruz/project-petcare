package com.project.petcare.dto;

import com.project.petcare.model.AppointmentStatus;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class AppointmentDTO {
    private Long id;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private LocalDate createdAt;
    private String reason;
    private AppointmentStatus status;
    private String appointmentNo;
    private PetOwnerDTO petOwner;
    private VeterinarianDTO veterinarian;
    private List<PetDTO> pets;
}