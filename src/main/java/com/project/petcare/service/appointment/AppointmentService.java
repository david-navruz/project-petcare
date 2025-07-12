package com.project.petcare.service.appointment;

import com.project.petcare.dto.AppointmentDTO;
import com.project.petcare.dto.EntityConverter;
import com.project.petcare.dto.PetDTO;
import com.project.petcare.exception.ResourceNotFoundException;
import com.project.petcare.model.Appointment;
import com.project.petcare.model.AppointmentStatus;
import com.project.petcare.model.Pet;
import com.project.petcare.model.User;
import com.project.petcare.repository.AppointmentRepository;
import com.project.petcare.repository.PetRepository;
import com.project.petcare.repository.UserRepository;
import com.project.petcare.request.AppointmentUpdateRequest;
import com.project.petcare.request.BookAppointmentRequest;
import com.project.petcare.service.pet.IPetService;
import com.project.petcare.utils.FeedBackMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AppointmentService implements IAppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final IPetService petService;
    private final EntityConverter<Appointment, AppointmentDTO> entityConverter;
    private final EntityConverter<Pet, PetDTO> petEntityConverter;


    @Transactional
    @Override
    public Appointment createAppointment(BookAppointmentRequest appointmentRequest, Long senderId, Long recipientId) {
        // Find the PetOwner who sends the appointment request
        Optional<User> sender = userRepository.findById(senderId);
        // Find the Veterinarian who will receive the request
        Optional<User> recipient = userRepository.findById(recipientId);
            if (sender.isPresent() && recipient.isPresent()){
                Appointment appointment = appointmentRequest.getAppointment();
                // Associating each pet with the appointment
                List<Pet> pets = appointmentRequest.getPets();
                pets.forEach(pet -> pet.setAppointment(appointment));
                List<Pet> savedPets = petService.savePets(pets);

                appointment.addPetOwner(sender.get());
                appointment.addVeterinarian(recipient.get());
                appointment.setAppointmentNo();
                appointment.setStatus(AppointmentStatus.WAITING_FOR_APPROVAL);
                // Adding the list of pets into the appointment
                appointment.setPets(savedPets);
                return appointmentRepository.save(appointment);
            }
        throw new ResourceNotFoundException(FeedBackMessage.SENDER_RECIPIENT_NOT_FOUND);
    }


    @Override
    public Appointment updateAppointment(Long id, AppointmentUpdateRequest updateRequest) {
        Appointment existingAppointment = getAppointmentById(id);
        // If already approved, we can no longer modify the appointment
            if (!Objects.equals(existingAppointment.getStatus(), AppointmentStatus.WAITING_FOR_APPROVAL)){
                throw new IllegalStateException(FeedBackMessage.ALREADY_APPROVED);
            }
            existingAppointment.setAppointmentDate(LocalDate.parse(updateRequest.getAppointmentDate()));
            existingAppointment.setAppointmentTime(LocalTime.parse(updateRequest.getAppointmentTime()));
            existingAppointment.setReason(updateRequest.getReason());
        return appointmentRepository.save(existingAppointment);
    }


    @Override
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }


    @Override
    public void deleteAppointment(Long id) {
        appointmentRepository.findById(id)
                .ifPresentOrElse(appointmentRepository::delete,
                        () -> {
                            throw new ResourceNotFoundException(FeedBackMessage.APPOINTMENT_NOT_FOUND);
                        });
    }


    @Override
    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(FeedBackMessage.APPOINTMENT_NOT_FOUND));
    }


    @Override
    public Appointment getAppointmentByNo(String appointmentNo) {
        return appointmentRepository.findByAppointmentNo(appointmentNo);
    }

    @Override
    public List<AppointmentDTO> getUserAppointments(Long userId) {
        List<Appointment> appointments = appointmentRepository.findAllByUserId(userId);
        return appointments.stream()
                .map(appointment -> {
                    AppointmentDTO appointmentDTO = entityConverter.mapEntityToDTO(appointment, AppointmentDTO.class);
                    if (appointmentDTO == null) {
                        throw new IllegalStateException("EntityConverter returned null AppointmentDTO");
                    }
                    List<PetDTO> petDTOS = appointment.getPets()
                            .stream()
                            .map(pet -> petEntityConverter.mapEntityToDTO(pet, PetDTO.class))
                            .toList();
                    appointmentDTO.setPets(petDTOS);
                    return appointmentDTO;
                }).toList();
    }


    @Override
    public Appointment cancelAppointment(Long appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .filter(app -> app.getStatus().equals(AppointmentStatus.WAITING_FOR_APPROVAL))
                .map(app -> {
                    app.setStatus(AppointmentStatus.CANCELLED);
                    return appointmentRepository.saveAndFlush(app);
                }).orElseThrow(() -> new IllegalStateException(FeedBackMessage.APPOINTMENT_UPDATE_NOT_ALLOWED));
    }


    @Override
    public Appointment approveAppointment(Long appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .filter(app -> app.getStatus().equals(AppointmentStatus.WAITING_FOR_APPROVAL))
                .map(app -> {
                    app.setStatus(AppointmentStatus.APPROVED);
                    return appointmentRepository.saveAndFlush(app);
                }).orElseThrow(() -> new IllegalStateException(FeedBackMessage.OPERATION_NOT_ALLOWED));
    }


    @Override
    public Appointment declineAppointment(Long appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .filter(appointment -> appointment.getStatus().equals(AppointmentStatus.WAITING_FOR_APPROVAL))
                .map(appointment -> {appointment.setStatus(AppointmentStatus.NOT_APPROVED);
                    return appointmentRepository.saveAndFlush(appointment);
                }).orElseThrow(() -> new IllegalStateException(FeedBackMessage.OPERATION_NOT_ALLOWED));
    }


    @Override
    public long countAppointment() {
        return appointmentRepository.count();
    }


    @Override
    public List<Map<String, Object>> getAppointmentSummary() {
        return getAllAppointments()
                .stream()
                .collect(Collectors.groupingBy(Appointment::getStatus, Collectors.counting()))
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() > 0)
                .map(entry -> createStatusSummaryMap(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }


    private Map<String, Object> createStatusSummaryMap(AppointmentStatus status, Long value){
        Map<String, Object> summaryMap = new HashMap<>();
        summaryMap.put("name", formatAppointmentStatus(status));
        summaryMap.put("value", value);
        return summaryMap;
    }

    private String formatAppointmentStatus(AppointmentStatus appointmentStatus) {
        return appointmentStatus.toString().replace("_", "-").toLowerCase();
    }




    @Override
    public List<Long> getAppointmentIds() {
        List<Appointment> appointments = getAllAppointments();
        return appointments.stream()
                .map(Appointment::getId)
                .toList();
    }


    @Override
    public void setAppointmentStatus(Long appointmentId) {
        Appointment appointment = getAppointmentById(appointmentId);
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        LocalTime appointmentEndTime = appointment.getAppointmentTime()
                .plusMinutes(2).truncatedTo(ChronoUnit.MINUTES);

        switch (appointment.getStatus()) {
            // Appointment is starting soon: UP_COMING
            case APPROVED:
                if (currentDate.isBefore(appointment.getAppointmentDate()) ||
                        (currentDate.isEqual(appointment.getAppointmentDate()) && currentTime.isBefore(appointment.getAppointmentTime()))) {
                    appointment.setStatus(AppointmentStatus.UP_COMING);
                }
                break;
            // Appointment is still going on: ON_GOING
            case UP_COMING:
                if (currentDate.equals(appointment.getAppointmentDate()) &&
                        currentTime.isAfter(appointment.getAppointmentTime()) && !currentTime.isAfter(appointmentEndTime)) {
                    appointment.setStatus(AppointmentStatus.ON_GOING);
                }
                break;
            // Appointment is completed: COMPLETED
            case ON_GOING:
                if (currentDate.isAfter(appointment.getAppointmentDate()) ||
                        (currentDate.equals(appointment.getAppointmentDate()) && !currentTime.isBefore(appointment.getAppointmentTime()))) {
                    appointment.setStatus(AppointmentStatus.COMPLETED);
                }
                break;
            // Appointment is still waiting for approval and the date has passed: NOT_APPROVED
            // Adjusted to change status to NOT_APPROVED if current time is past the appointment time
            case WAITING_FOR_APPROVAL:
                if (currentDate.isAfter(appointment.getAppointmentDate()) ||
                        (currentDate.equals(appointment.getAppointmentDate()) && currentTime.isAfter(appointment.getAppointmentTime()))) {
                    appointment.setStatus(AppointmentStatus.NOT_APPROVED);
                }
                break;
        }
        appointmentRepository.save(appointment);
    }


}
