package com.project.petcare.repository;

import com.project.petcare.model.Appointment;
import com.project.petcare.model.AppointmentStatus;
import com.project.petcare.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    Appointment findByAppointmentNo(String appointmentNo);

    boolean existsByVeterinarianIdAndPetOwnerIdAndStatus(Long veterinarianId, Long reviewerId, AppointmentStatus appointmentStatus);

    @Query("SELECT a FROM Appointment a WHERE a.petOwner.id =:userId OR a.veterinarian.id =:userId ")
    List<Appointment> findAllByUserId(@Param("userId") Long userId);

    List<Appointment> findByVeterinarianAndAppointmentDate(User veterinarian, LocalDate requestedDate);

}
