package com.project.petcare.service.veterinarian;

import com.project.petcare.dto.EntityConverter;
import com.project.petcare.dto.UserDTO;
import com.project.petcare.exception.ResourceNotFoundException;
import com.project.petcare.model.Appointment;
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
        List<Veterinarian> filteredVets = getAvailableVeterinarians(specialization, date, time);
        return filteredVets.stream()
                .map(this::mapVeterinarianToUserDto)
                .toList();
    }


    @Override
    public List<Veterinarian> getVeterinariansBySpecialization(String specialization) {
        if (!veterinarianRepository.existsBySpecialization(specialization)){
            throw new ResourceNotFoundException("No veterinarian found with" +specialization +" in the system");
        }
        return veterinarianRepository.findBySpecialization(specialization);
    }


    @Override
    public List<Map<String, Object>> aggregateVetsBySpecialization() {
        List<Object[]> results = veterinarianRepository.countVetsBySpecialization();
        return results.stream()
                .map(result -> Map.of("specialization", result[0], "count", result[1]))
                .toList();
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
    private List<Veterinarian> getAvailableVeterinarians(String specialization, LocalDate date, LocalTime time){
        List<Veterinarian> veterinarians = getVeterinariansBySpecialization(specialization);
        return veterinarians.stream()
                .filter(vet -> isVetAvailable(vet, date, time))
                .toList();
    }

    // Helper Method : check if the Vet is available for a given date and time
    private boolean isVetAvailable(Veterinarian veterinarian, LocalDate requestedDate, LocalTime requestedTime) {
        if (requestedDate != null && requestedTime != null) {
            LocalTime requestedEndTime = requestedTime.plusHours(2);
            return appointmentRepository.findByVeterinarianAndAppointmentDate(veterinarian, requestedDate)
                    .stream()
                    .noneMatch(existingApp -> doesAppointmentOverLap(existingApp, requestedTime, requestedEndTime));
        }
        return true;
    }

    // Helper Method : check if there is already an appointment set for a given date and time
    /** Vérifie si le créneau horaire demandé chevauche un rendez-vous existant.
     *  Logique : Un rendez-vous dure 2 heures.
     *  Le vétérinaire est indisponible :   1 heure avant le début du rendez-vous existant.
     *                                      2h50 (170 minutes) après la fin du rendez-vous.
     *  Le créneau est en conflit si :      Il commence après ou pendant l’intervalle d’indisponibilité.
     *                                      Et se termine avant ou pendant ce même intervalle.
     *  Cela simule une zone tampon avant et après chaque rendez-vous pour inclure des préparations ou déplacements.
     * @param existingAppointment
     * @param requestedStartTime
     * @param requestedEndTime
     * @return
     */
    private boolean doesAppointmentOverLap(Appointment existingAppointment, LocalTime requestedStartTime, LocalTime requestedEndTime){
        LocalTime existingStartTime = existingAppointment.getAppointmentTime();
        LocalTime existingEndTime = existingStartTime.plusHours(2); // Un rendez-vous dure 2 heures.
        LocalTime unavailableStartTime = existingStartTime.minusHours(1);
        LocalTime unavailableEndTime = existingEndTime.plusMinutes(170);
        return !requestedStartTime.isBefore(unavailableStartTime) && !requestedEndTime.isAfter(unavailableEndTime);
    }


}
