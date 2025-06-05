package com.project.petcare.controller;

import com.project.petcare.exception.ResourceNotFoundException;
import com.project.petcare.model.Pet;
import com.project.petcare.response.APIResponse;
import com.project.petcare.service.pet.IPetService;
import com.project.petcare.utils.FeedBackMessage;
import com.project.petcare.utils.UrlMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RequiredArgsConstructor
@RequestMapping(UrlMapping.PETS)
@RestController
public class PetController {

    private final IPetService petService;


    @PostMapping(UrlMapping.SAVE_PETS_FOR_APPOINTMENT)
    public ResponseEntity<APIResponse> savePets(@RequestParam Long appointmentId, @RequestBody List<Pet> pets) {
        try {
            List<Pet> savedPets = petService.savePetsForAppointment(appointmentId, pets);
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.PET_ADDED_SUCCESS, savedPets));
        } catch (RuntimeException e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(e.getMessage(), null));
        }
    }


    @GetMapping(UrlMapping.GET_PET_BY_ID)
    public ResponseEntity<APIResponse> getPetById(@PathVariable Long petId) {
        try {
            Pet pet = petService.getPetById(petId);
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.PET_FOUND, pet));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new APIResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(e.getMessage(), null));
        }
    }


    @PutMapping(UrlMapping.UPDATE_PET)
    public ResponseEntity<APIResponse> updatePet(@PathVariable Long petId, @RequestBody Pet pet) {
        try {
            Pet thePet = petService.updatePet(petId, pet);
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.PET_UPDATE_SUCCESS, thePet));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new APIResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(e.getMessage(), null));
        }
    }


    @DeleteMapping(UrlMapping.DELETE_PET_BY_ID)
    public ResponseEntity<APIResponse> deletePetById(@PathVariable Long petId) {
        try {
            petService.deletePet(petId);
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.PET_DELETE_SUCCESS, null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new APIResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(e.getMessage(), null));
        }
    }


    @GetMapping(UrlMapping.GET_PET_TYPES)
    public ResponseEntity<APIResponse> getAllPetTypes(){
        return ResponseEntity.ok(new APIResponse(FeedBackMessage.RESOURCE_FOUND, petService.getPetTypes()));
    }


    @GetMapping(UrlMapping.GET_PET_COLORS)
    public ResponseEntity<APIResponse> getAllPetColors(){
        return ResponseEntity.ok(new APIResponse(FeedBackMessage.RESOURCE_FOUND, petService.getPetColors()));
    }


    @GetMapping(UrlMapping.GET_PET_BREEDS)
    public ResponseEntity<APIResponse> getAllPetBreeds(@RequestParam String petType){
        return ResponseEntity.ok(new APIResponse(FeedBackMessage.RESOURCE_FOUND, petService.getPetBreeds(petType)));
    }

}
