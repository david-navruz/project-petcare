package com.project.petcare.event;

import com.project.petcare.model.Appointment;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class AppointmentBookedEvent extends ApplicationEvent {
    private Appointment appointment;

    public AppointmentBookedEvent(Appointment appointment ) {
        super(appointment);
        this.appointment = appointment;
    }
}
