package com.project.petcare.controller;

import com.project.petcare.event.AppointmentApprovedEvent;
import com.project.petcare.event.AppointmentDeclinedEvent;
import com.project.petcare.exception.ResourceNotFoundException;
import com.project.petcare.model.Appointment;
import com.project.petcare.request.AppointmentUpdateRequest;
import com.project.petcare.request.BookAppointmentRequest;
import com.project.petcare.response.APIResponse;
import com.project.petcare.service.appointment.IAppointmentService;
import com.project.petcare.utils.FeedBackMessage;
import com.project.petcare.utils.UrlMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@RequestMapping(UrlMapping.APPOINTMENTS)
@RestController
public class AppointmentController {

    private final IAppointmentService appointmentService;
    private  final ApplicationEventPublisher publisher;


    @PostMapping(UrlMapping.BOOK_APPOINTMENT)
    public ResponseEntity<APIResponse> bookAppointment(
            @RequestBody BookAppointmentRequest appointmentRequest,
            @RequestParam Long senderId,
            @RequestParam Long recipientId) {
        try {
            Appointment theAppointment = appointmentService.createAppointment(appointmentRequest, senderId, recipientId);
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.SUCCESS, theAppointment));

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new APIResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(e.getMessage(), null));
        }
    }


    @PutMapping(UrlMapping.UPDATE_APPOINTMENT)
    public ResponseEntity<APIResponse> updateAppointment(
            @PathVariable Long id,
            @RequestBody AppointmentUpdateRequest request) {
        try {
            Appointment appointment = appointmentService.updateAppointment(id, request);
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.APPOINTMENT_UPDATE_SUCCESS, appointment));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(NOT_ACCEPTABLE).body(new APIResponse(e.getMessage(), null));
        }
    }


    @GetMapping(UrlMapping.ALL_APPOINTMENT)
    public ResponseEntity<APIResponse> getAllAppointments() {
        try {
            List<Appointment> appointments = appointmentService.getAllAppointments();
            return ResponseEntity.status(FOUND).body(new APIResponse(FeedBackMessage.APPOINTMENT_FOUND, appointments));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(e.getMessage(), null));
        }
    }


    @GetMapping(UrlMapping.GET_APPOINTMENT_BY_ID)
    public ResponseEntity<APIResponse> getAppointmentById(@PathVariable Long id) {
        try {
            Appointment appointment = appointmentService.getAppointmentById(id);
            return ResponseEntity.status(FOUND).body(new APIResponse(FeedBackMessage.APPOINTMENT_FOUND, appointment));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new APIResponse(e.getMessage(), null));
        }
    }


    @GetMapping(UrlMapping.GET_APPOINTMENT_BY_NO)
    public ResponseEntity<APIResponse> getAppointmentByNo(@PathVariable String appointmentNo) {
        try {
            Appointment appointment = appointmentService.getAppointmentByNo(appointmentNo);
            return ResponseEntity.status(FOUND).body(new APIResponse(FeedBackMessage.APPOINTMENT_FOUND, appointment));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new APIResponse(e.getMessage(), null));
        }
    }


    @DeleteMapping(UrlMapping.DELETE_APPOINTMENT)
    public ResponseEntity<APIResponse> deleteAppointmentById(@PathVariable Long id) {
        try {
            appointmentService.deleteAppointment(id);
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.APPOINTMENT_DELETE_SUCCESS, null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new APIResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(e.getMessage(), null));
        }
    }



    @PutMapping(UrlMapping.CANCEL_APPOINTMENT)
    public ResponseEntity<APIResponse> cancelAppointment(@PathVariable Long id) {
        try {
            Appointment appointment = appointmentService.cancelAppointment(id);
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.APPOINTMENT_CANCELLED_SUCCESS, appointment));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(NOT_ACCEPTABLE).body(new APIResponse(e.getMessage(), null));
        }

    }


    @PutMapping(UrlMapping.APPROVE_APPOINTMENT)
    public ResponseEntity<APIResponse> approveAppointment(@PathVariable Long id) {
        try {
            Appointment appointment = appointmentService.approveAppointment(id);
            publisher.publishEvent(new AppointmentApprovedEvent(appointment));
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.APPOINTMENT_APPROVED_SUCCESS, appointment));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(NOT_ACCEPTABLE).body(new APIResponse(e.getMessage(), null));
        }
    }


    @PutMapping(UrlMapping.DECLINE_APPOINTMENT)
    public ResponseEntity<APIResponse> declineAppointment(@PathVariable Long id) {
        try {
            Appointment appointment = appointmentService.declineAppointment(id);
            publisher.publishEvent(new AppointmentDeclinedEvent(appointment));
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.APPOINTMENT_DECLINED_SUCCESS, appointment));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(NOT_ACCEPTABLE).body(new APIResponse(e.getMessage(), null));
        }
    }


    @GetMapping(UrlMapping.COUNT_APPOINTMENT)
    public long countAppointments() {
        return appointmentService.countAppointment();
    }


    @GetMapping(UrlMapping.GET_APPOINTMENT_SUMMARY)
    public ResponseEntity<APIResponse> getAppointmentSummary() {
        try {
            List<Map<String, Object>> summary = appointmentService.getAppointmentSummary();
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.SUCCESS, summary));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(FeedBackMessage.ERROR + e.getMessage(), null));
        }
    }

}
