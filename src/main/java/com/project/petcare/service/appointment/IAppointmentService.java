package com.project.petcare.service.appointment;

import com.project.petcare.model.Appointment;
import com.project.petcare.request.AppointmentUpdateRequest;

import java.util.List;

public interface IAppointmentService {

    Appointment createAppointment(Appointment appointment, Long senderId, Long recipientId);

    Appointment updateAppointment(Long id, AppointmentUpdateRequest updateRequest);

    List<Appointment> getAllAppointments();

    void deleteAppointment(Long id);

    Appointment getAppointmentById(Long id);

    Appointment getAppointmentByNo(String appointmentNo);

}
