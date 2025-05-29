package com.project.petcare.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
//  empêche la sérialisation JSON pour éviter des cycles infinis
//  ou des données non nécessaires lors des appels REST
@JsonIgnoreProperties({"appointment"})
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type;
    private String color;
    private String breed;
    private int age;

    @ManyToOne
    private Appointment appointment;

}
