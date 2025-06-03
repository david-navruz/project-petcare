package com.project.petcare.controller;

import com.project.petcare.dto.EntityConverter;
import com.project.petcare.dto.UserDTO;
import com.project.petcare.exception.UserAlreadyExistsException;
import com.project.petcare.model.User;
import com.project.petcare.request.RegistrationRequest;
import com.project.petcare.response.APIResponse;
import com.project.petcare.service.user.UserService;
import com.project.petcare.utils.FeedBackMessage;
import com.project.petcare.utils.UrlMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RequiredArgsConstructor
@RequestMapping(UrlMapping.USERS)
@RestController
public class UserController {

    private final UserService userService;
    private final EntityConverter<User, UserDTO> entityConverter;


    @PostMapping
    public ResponseEntity<APIResponse> createUser(@RequestBody RegistrationRequest request) {
        try {
            User theUser = userService.createNewUser(request);
            UserDTO savedUser = entityConverter.mapEntityToDTO(theUser, UserDTO.class);
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.CREATE_USER_SUCCESS, savedUser));
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(CONFLICT).body(new APIResponse(FeedBackMessage.USER_ALREADY_EXISTS, null));
        }
        catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(e.getMessage(), null));
        }
    }



}
