package com.project.petcare.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@JsonIgnoreProperties({"petOwner","veterinarian"})
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String reason;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate appointmentDate;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime appointmentTime;

    private String appointmentNo;

    @CreationTimestamp
    private LocalDate createdAt;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    @JoinColumn(name = "sender")
    @ManyToOne(fetch = FetchType.LAZY)
    private User petOwner;

    @JoinColumn(name = "recipient")
    @ManyToOne(fetch = FetchType.LAZY)
    private User veterinarian;

    @OneToMany(mappedBy = "appointment",cascade = CascadeType.ALL)
    List<Pet> pets = new ArrayList<>();


    /** Methode pour ajouter un PetOwner pour un rendez-vous
     * @param sender
     */
    public void addPetOwner(User sender){
        this.setPetOwner(sender);
        if (sender.getAppointments() == null){
            sender.setAppointments(new ArrayList<>());
        }
        sender.getAppointments().add(this);
    }

    /** Methode pour ajouter un Veterinarian pour un rendez-vous
     * @param recipient
     */
    public void addVeterinarian(User recipient){
        this.setVeterinarian(recipient);
        if (recipient.getAppointments() == null){
            recipient.setAppointments(new ArrayList<>());
        }
        recipient.getAppointments().add(this);
    }

    /** Methode pour générer un rendez-vous number
     */
    public void setAppointmentNo(){
        this.appointmentNo = String.valueOf(new Random().nextLong()).substring(1,11);
    }

}
