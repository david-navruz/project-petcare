package com.project.petcare.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String gender;
    @Column(name = "mobile")
    private String phoneNumber;
    private String email;
    private String password;
    private String userType;
    private boolean isEnabled;

    @CreationTimestamp
    private LocalDate createdAt;

    @Transient
    private String specialization;

    @Transient
    private List<Appointment> appointments = new ArrayList<>();

    @Transient
    private List<Review> reviews = new ArrayList<>();

    // orphanRemoval=true: si tu fais user.setPhoto(null), la photo est supprimée de la base automatiquement
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Photo photo;


    // ManyToMany: un user peut avoir plusieurs rôles, et un rôle peut appartenir à plusieurs users
    // Cascade: les rôles seront attachés ou détachés automatiquement dans certains cas
    // (pas REMOVE ici donc la suppression d’un user ne supprime pas les rôles).
    // EAGER: les rôles sont chargés immédiatement avec user
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.MERGE,
            CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name ="user_roles",
            joinColumns = @JoinColumn(name="user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name="role_id", referencedColumnName = "id"))
    private Collection<Role> roles = new HashSet<>();


    // Un user peut avoir plusieurs tokens de vérification.
    // mappedBy="user": le champ user est défini dans la classe VerificationToken, c’est lui qui possède la relation.
    // cascade = CascadeType.REMOVE: si tu supprimes un user, tous ses tokens sont aussi supprimés automatiquement.
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<VerificationToken> verificationTokens = new ArrayList<>();

    // Grâce à orphanRemoval = true, cela supprime aussi la photo de la base si elle n’est référencée nulle part ailleurs.
    public void removeUserPhoto(){
        if(this.getPhoto() != null){
            this.setPhoto(null);
        }
    }

}
