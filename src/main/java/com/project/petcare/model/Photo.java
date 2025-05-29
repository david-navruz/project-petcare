package com.project.petcare.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Blob;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileType;
    private String fileName;

    // Lob indique à JPA que c'est un Large Object
    // Blob (Binary Large Object) est un type SQL qui stocke des données binaires volumineuses dans la base
    @Lob
    private Blob image;

}
