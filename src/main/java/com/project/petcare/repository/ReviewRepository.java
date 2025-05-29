package com.project.petcare.repository;

import com.project.petcare.model.Review;
import com.project.petcare.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Pour retrouver les reviews d'un vétérinaire
    List<Review> findByVeterinarian(User vet);

    // Pour les reviews rédigées par un user
    List<Review> findByPetOwner(User petOwner);

}
