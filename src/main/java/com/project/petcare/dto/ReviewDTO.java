package com.project.petcare.dto;

import lombok.Data;

@Data
public class ReviewDTO {
    private Long id;
    private int stars;
    private String feedback;
    private Long veterinarianId;
    private String veterinarianName;
    private Long petOwnerId;
    private String petOwnerName;
    private byte[] petOwnerImage;
    private byte[] veterinarianImage;
}
