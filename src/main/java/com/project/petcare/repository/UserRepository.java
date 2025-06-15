package com.project.petcare.repository;

import com.project.petcare.model.User;
import com.project.petcare.model.Veterinarian;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.firstName =:firstName, u.lastName=:lastName, " +
            "u.gender=:gender, u.phoneNumber=:phoneNumber")
    User updateUser(@Param("userId") Long userId,
                    @Param("firstName") String firstName,
                    @Param("lastName") String lastName,
                    @Param("gender") String gender,
                    @Param("phoneNumber") String phoneNumber);

    List<Veterinarian> findAllByUserType(String vet);

    long countByUserType(String type);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.isEnabled = :enabled where u.id = :userId")
    void updateUserEnabledStatus(@Param("userId")Long userId, @Param("enabled") boolean enabled);

}
