package com.project.petcare.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Optional;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String feedback;
    private int stars;

    @ManyToOne
    @JoinColumn(name = "veterinarian_id")
    private User veterinarian;

    @ManyToOne
    @JoinColumn(name = "reviewer_id")
    private User petOwner;

    public void removeRelationShip() {
        Optional.ofNullable(veterinarian)
                .ifPresent(vet -> vet.getReviews().remove(this));
        Optional.ofNullable(petOwner)
                .ifPresent(petOwner -> petOwner.getReviews()
                        .remove(this));
    }

}
