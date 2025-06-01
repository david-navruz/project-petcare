package com.project.petcare.model;

import com.project.petcare.utils.SystemUtils;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@Entity
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;
    private Date expirationDate;

    // plusieurs tokens peuvent être associées à un seul user
    // la colonne user_id sera utilisée dans la table de cette entité pour faire la jointure avec la table user.
    // Cette colonne agit comme une foreign key pointant vers la colonne id dans la table user.
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public VerificationToken(String token, User user) {
        this.token = token;
        this.user = user;
        this.expirationDate = SystemUtils.getExpirationTime();
    }
}
