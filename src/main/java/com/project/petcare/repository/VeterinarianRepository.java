package com.project.petcare.repository;

import com.project.petcare.model.Veterinarian;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VeterinarianRepository extends JpaRepository<Veterinarian, Long> {

}
