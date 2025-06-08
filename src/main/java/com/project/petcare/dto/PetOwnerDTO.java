package com.project.petcare.dto;

import lombok.Data;

@Data
public class PetOwnerDTO {
    private Long petOwnerId;
    private String firstName;
    private String lastName;
    private String email;
    private String gender;
    private String phoneNumber;
}
