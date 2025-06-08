package com.project.petcare.service.veterinarian;

import com.project.petcare.dto.EntityConverter;
import com.project.petcare.dto.UserDTO;
import com.project.petcare.model.User;
import com.project.petcare.model.Veterinarian;
import com.project.petcare.repository.AppointmentRepository;
import com.project.petcare.repository.UserRepository;
import com.project.petcare.repository.VeterinarianRepository;
import com.project.petcare.service.photo.IPhotoService;
import com.project.petcare.service.review.IReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class VeterinarianService implements IVeterinarianService {

    private final VeterinarianRepository veterinarianRepository;
    private final UserRepository userRepository;
    private final IReviewService reviewService;
    private final IPhotoService photoService;
    private final AppointmentRepository appointmentRepository;
    private final EntityConverter<Veterinarian, UserDTO> entityConverter;


    @Override
    public List<UserDTO> getAllVeterinariansWithDetails() {
        List<Veterinarian> veterinarians = userRepository.findAllByUserType("VET");
        return veterinarians.stream()
                .map(this::mapVeterinarianToUserDto)
                .toList();
    }


    @Override
    public List<String> getSpecializations() {
        return veterinarianRepository.getSpecializations();
    }


    @Override
    public List<UserDTO> findAvailableVetsForAppointment(String specialization, LocalDate date, LocalTime time) {


        return List.of();
    }


    @Override
    public List<Veterinarian> getVeterinariansBySpecialization(String specialization) {
        return List.of();
    }


    @Override
    public List<Map<String, Object>> aggregateVetsBySpecialization() {
        return List.of();
    }


    // Helper Method : Transforming a Vet into a UserDTO with all the info such as AverageRating, Total Reviewer Count
    private UserDTO mapVeterinarianToUserDto(Veterinarian veterinarian) {
        UserDTO userDTO = entityConverter.mapEntityToDTO(veterinarian, UserDTO.class);
        double averageRating = reviewService.getAverageRatingForVet(veterinarian.getId());
        Long totalReviewer = reviewService.countByVeterinarianId(veterinarian.getId());
        // Updating the userDto
        userDTO.setAverageRating(averageRating);
        userDTO.setTotalReviewers(totalReviewer);
        if (veterinarian.getPhoto() != null) {
            try {
                byte[] photoByte = photoService.getPhotoData(veterinarian.getPhoto().getId());
                userDTO.setPhoto(photoByte);
            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        return userDTO;
    }


    // Helper Method : getting the list of available Vets




    // Helper Method : check if the Vet is available for a given date and time




    // Helper Method : check if there is already an appointment set for a given date and time







}
