package com.project.petcare.service.veterinarian;

import com.project.petcare.dto.UserDTO;
import com.project.petcare.model.Veterinarian;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public interface IVeterinarianService {
    List<UserDTO> getAllVeterinariansWithDetails();

    List<String> getSpecializations();

    List<UserDTO> findAvailableVetsForAppointment(String specialization, LocalDate date, LocalTime time);

    List<Veterinarian> getVeterinariansBySpecialization(String specialization);

    List<Map<String, Object>> aggregateVetsBySpecialization();
}
