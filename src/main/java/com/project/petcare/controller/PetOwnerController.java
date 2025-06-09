package com.project.petcare.controller;

import com.project.petcare.dto.UserDTO;
import com.project.petcare.response.APIResponse;
import com.project.petcare.service.petowner.IPetOwnerService;
import com.project.petcare.utils.FeedBackMessage;
import com.project.petcare.utils.UrlMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController(UrlMapping.PETOWNERS)
public class PetOwnerController {

    private final IPetOwnerService petOwnerService;

    @GetMapping(UrlMapping.GET_ALL_PETOWNERS)
    public ResponseEntity<APIResponse> getAllPetOwners() {
        List<UserDTO> petOwnerDTOS = petOwnerService.getPetOwners();
        return ResponseEntity.ok(new APIResponse(FeedBackMessage.RESOURCE_FOUND, petOwnerDTOS));
    }

}
