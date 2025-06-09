package com.project.petcare.dto;

import lombok.Data;

@Data
public class VeterinarianDTO {
    private Long veterinarianId;
    private String firstName;
    private String lastName;
    private String email;
    private String gender;
    private String phoneNumber;
    private String specialization;
}