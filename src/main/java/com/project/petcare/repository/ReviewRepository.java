package com.project.petcare.repository;

import com.project.petcare.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Pour retrouver les reviews d'un vétérinaire
    List<Review> findByVeterinarianId(Long veterinarianId);

    // Pour les reviews rédigées par un user
    List<Review> findByPetOwnerId(Long petOwnerId);

    @Query("SELECT r FROM Review r WHERE r.petOwner.id =:userId OR r.veterinarian.id =:userId ")
    Page<Review> findAllByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.petOwner.id =:userId OR r.veterinarian.id =:userId ")
    List<Review> findAllByUserId(@Param("userId") Long userId);

    Optional<Review> findByVeterinarianIdAndPetOwnerId(Long veterinarianId, Long reviewerId);

    Long countByVeterinarianId(Long id);

}
