package com.project.petcare.controller;

import com.project.petcare.exception.ResourceNotFoundException;
import com.project.petcare.model.Appointment;
import com.project.petcare.request.AppointmentUpdateRequest;
import com.project.petcare.response.APIResponse;
import com.project.petcare.service.appointment.AppointmentService;
import com.project.petcare.utils.FeedBackMessage;
import com.project.petcare.utils.UrlMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@RequestMapping(UrlMapping.APPOINTMENTS)
@RestController
public class AppointmentController {

    private final AppointmentService appointmentService;


    @PostMapping(UrlMapping.BOOK_APPOINTMENT)
    public ResponseEntity<APIResponse> bookAppointment(
            @RequestBody Appointment appointment,
            @RequestParam Long senderId,
            @RequestParam Long recipientId) {
        try {
            Appointment theAppointment = appointmentService.createAppointment(appointment, senderId, recipientId);
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



}
