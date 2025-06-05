package com.project.petcare.service.pet;

import com.project.petcare.model.Pet;

import java.util.List;

public interface IPetService {

    List<Pet> savePets(List<Pet> pets);

    List<Pet> savePetsForAppointment(Long appointmentId, List<Pet> pets);

    Pet updatePet(Long idPet, Pet pet);

    void deletePet(Long idPet);

    Pet getPetById(Long idPet);

    List<String> getPetTypes();

    List<String> getPetColors();

    List<String> getPetBreeds(String petType);

}
