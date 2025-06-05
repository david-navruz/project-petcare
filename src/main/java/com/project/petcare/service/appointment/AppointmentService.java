package com.project.petcare.service.appointment;

import com.project.petcare.exception.ResourceNotFoundException;
import com.project.petcare.model.Appointment;
import com.project.petcare.model.AppointmentStatus;
import com.project.petcare.model.User;
import com.project.petcare.repository.AppointmentRepository;
import com.project.petcare.repository.UserRepository;
import com.project.petcare.request.AppointmentUpdateRequest;
import com.project.petcare.utils.FeedBackMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AppointmentService implements IAppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;


    @Override
    public Appointment createAppointment(Appointment appointment, Long senderId, Long recipientId) {
        // Find the PetOwner who sends the appointment request
        Optional<User> sender = userRepository.findById(senderId);
        // Find the Veterinarian who will receive the request
        Optional<User> recipient = userRepository.findById(recipientId);
            if (sender.isPresent() && recipient.isPresent()){
                appointment.addPetOwner(sender.get());
                appointment.addVeterinarian(recipient.get());
                appointment.setAppointmentNo();
                appointment.setStatus(AppointmentStatus.WAITING_FOR_APPROVAL);
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

}
