package com.project.petcare.service.appointment;

import com.project.petcare.dto.AppointmentDTO;
import com.project.petcare.model.Appointment;
import com.project.petcare.request.AppointmentUpdateRequest;
import com.project.petcare.request.BookAppointmentRequest;

import java.util.List;
import java.util.Map;

public interface IAppointmentService {

    Appointment createAppointment(BookAppointmentRequest appointmentRequest, Long senderId, Long recipientId);

    Appointment updateAppointment(Long id, AppointmentUpdateRequest updateRequest);

    List<Appointment> getAllAppointments();

    void deleteAppointment(Long id);

    Appointment getAppointmentById(Long id);

    Appointment getAppointmentByNo(String appointmentNo);

    List<AppointmentDTO> getUserAppointments(Long userId);

    Appointment cancelAppointment(Long appointmentId);

    Appointment approveAppointment(Long appointmentId);

    Appointment declineAppointment(Long appointmentId);

    long countAppointment();

    List<Map<String, Object>> getAppointmentSummary();

    List<Long> getAppointmentIds();

    void setAppointmentStatus(Long appointmentId);

}
