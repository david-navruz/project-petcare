package com.project.petcare.request;

import com.project.petcare.model.Appointment;
import com.project.petcare.model.Pet;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class BookAppointmentRequest {
    private Appointment appointment;
    private List<Pet> pets;
}
