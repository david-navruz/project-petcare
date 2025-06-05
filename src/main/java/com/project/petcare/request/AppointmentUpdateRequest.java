package com.project.petcare.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class AppointmentUpdateRequest {
    private String appointmentDate;
    private String appointmentTime;
    private String reason;
}
