package com.project.petcare.service.pet;

import com.project.petcare.exception.ResourceNotFoundException;
import com.project.petcare.model.Appointment;
import com.project.petcare.model.Pet;
import com.project.petcare.repository.AppointmentRepository;
import com.project.petcare.repository.PetRepository;
import com.project.petcare.utils.FeedBackMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PetService implements IPetService {

    private final PetRepository petRepository;
    private final AppointmentRepository appointmentRepository;


    @Override
    public List<Pet> savePets(List<Pet> pets) {
        return petRepository.saveAll(pets);
    }


    @Override
    public List<Pet> savePetsForAppointment(Long appointmentId, List<Pet> pets) {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow(null);
        return pets.stream()
                .peek(pet -> pet.setAppointment(appointment))
                .map(petRepository::save)
                .collect(Collectors.toList());
    }


    @Override
    public Pet updatePet(Long idPet, Pet pet) {
        Pet existingPet = getPetById(idPet);
        existingPet.setName(pet.getName());
        existingPet.setAge(pet.getAge());
        existingPet.setColor(pet.getColor());
        existingPet.setType(pet.getType());
        existingPet.setBreed(pet.getBreed());
        return petRepository.save(existingPet);
    }


    @Override
    public void deletePet(Long idPet) {
        petRepository.findById(idPet)
                .ifPresentOrElse(petRepository::delete,
                                    () -> {
                                        throw new ResourceNotFoundException(FeedBackMessage.APPOINTMENT_NOT_FOUND);
                                    });
    }


    @Override
    public Pet getPetById(Long petId) {
        return petRepository.findById(petId)
                .orElseThrow(() -> new ResourceNotFoundException(FeedBackMessage.RESOURCE_NOT_FOUND));
    }


    @Override
    public List<String> getPetTypes() {
        return petRepository.getDistinctPetTypes();
    }


    @Override
    public List<String> getPetColors() {
        return petRepository.getDistinctPetColors();
    }


    @Override
    public List<String> getPetBreeds(String petType) {
        return petRepository.getDistinctPetBreedByPetType(petType);
    }

}
