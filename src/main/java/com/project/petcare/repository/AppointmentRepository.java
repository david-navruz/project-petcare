package com.project.petcare.repository;

import com.project.petcare.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    public Appointment findByAppointmentNo(String appointmentNo);

}
