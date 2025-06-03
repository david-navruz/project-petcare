package com.project.petcare.controller;

import com.project.petcare.dto.EntityConverter;
import com.project.petcare.dto.UserDTO;
import com.project.petcare.exception.ResourceNotFoundException;
import com.project.petcare.exception.UserAlreadyExistsException;
import com.project.petcare.model.User;
import com.project.petcare.request.RegistrationRequest;
import com.project.petcare.request.UserUpdateRequest;
import com.project.petcare.response.APIResponse;
import com.project.petcare.service.user.UserService;
import com.project.petcare.utils.FeedBackMessage;
import com.project.petcare.utils.UrlMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@RequestMapping(UrlMapping.USERS)
@RestController
public class UserController {

    private final UserService userService;
    private final EntityConverter<User, UserDTO> entityConverter;


    @PostMapping(UrlMapping.REGISTER_USER)
    public ResponseEntity<APIResponse> register(@RequestBody RegistrationRequest request) {
        try {
            User theUser = userService.createNewUser(request);
            UserDTO savedUser = entityConverter.mapEntityToDTO(theUser, UserDTO.class);
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.CREATE_USER_SUCCESS, savedUser));
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(CONFLICT).body(new APIResponse(FeedBackMessage.USER_ALREADY_EXISTS, null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(e.getMessage(), null));
        }
    }


    @PutMapping(UrlMapping.UPDATE_USER)
    public ResponseEntity<APIResponse> update(@PathVariable Long userId, @RequestBody UserUpdateRequest request) {
        try {
            User theUser = userService.updateUser(userId, request);
            UserDTO updatedUser = entityConverter.mapEntityToDTO(theUser, UserDTO.class);
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.USER_UPDATE_SUCCESS, updatedUser));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new APIResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(e.getMessage(), null));
        }
    }


    @GetMapping(UrlMapping.GET_USER_BY_ID)
    public ResponseEntity<APIResponse> getUserById(@PathVariable Long userId) {
        try {
            User foundUser = userService.findUserById(userId);
            UserDTO userDto = entityConverter.mapEntityToDTO(foundUser, UserDTO.class);
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.USER_FOUND, userDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new APIResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(e.getMessage(), null));
        }
    }


    @DeleteMapping(UrlMapping.DELETE_USER_BY_ID)
    public ResponseEntity<APIResponse> deleteById(@PathVariable Long userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.DELETE_USER_SUCCESS, null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new APIResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(e.getMessage(), null));
        }
    }


    @GetMapping(UrlMapping.GET_ALL_USERS)
    public ResponseEntity<APIResponse> getAllUsers() {
        try {
            List<UserDTO> users = userService.getAllUsers();
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.USER_FOUND, users));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(e.getMessage(), null));
        }
    }





}
