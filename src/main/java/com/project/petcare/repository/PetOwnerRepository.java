package com.project.petcare.repository;

import com.project.petcare.model.PetOwner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetOwnerRepository extends JpaRepository<PetOwner, Long> {

}
