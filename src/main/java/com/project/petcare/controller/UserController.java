package com.project.petcare.controller;

import com.project.petcare.dto.EntityConverter;
import com.project.petcare.dto.UserDTO;
import com.project.petcare.event.RegistrationCompleteEvent;
import com.project.petcare.exception.ResourceNotFoundException;
import com.project.petcare.exception.UserAlreadyExistsException;
import com.project.petcare.model.User;
import com.project.petcare.request.ChangePasswordRequest;
import com.project.petcare.request.RegistrationRequest;
import com.project.petcare.request.UserUpdateRequest;
import com.project.petcare.response.APIResponse;
import com.project.petcare.service.password.IChangePasswordService;
import com.project.petcare.service.user.UserService;
import com.project.petcare.utils.FeedBackMessage;
import com.project.petcare.utils.UrlMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@RequestMapping(UrlMapping.USERS)
@RestController
public class UserController {

    private final UserService userService;
    private final IChangePasswordService changePasswordService;
    private final EntityConverter<User, UserDTO> entityConverter;
    private final ApplicationEventPublisher publisher;


    @PostMapping(UrlMapping.REGISTER_USER)
    public ResponseEntity<APIResponse> register(@RequestBody RegistrationRequest request) {
        try {
            User theUser = userService.createNewUser(request);
            publisher.publishEvent(new RegistrationCompleteEvent(theUser));
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
            UserDTO userDto = userService.getUserWithDetails(userId);
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

    @GetMapping(UrlMapping.COUNT_ALL_VETS)
    public long countVeterinarians() {
        return userService.countVeterinarians();
    }
    @GetMapping(UrlMapping.COUNT_ALL_PETOWNERS)
    public long countPetOwners() {
        return userService.countPetOwners();
    }

    @GetMapping(UrlMapping.COUNT_ALL_USERS)
    public long countUsers(){
        return userService.countAllUsers();
    }


    @GetMapping(UrlMapping.AGGREGATE_USERS)
    public ResponseEntity<APIResponse> aggregateUsersByMonthAndType(){
        try {
            Map<String, Map<String, Long>> aggregatedUsers = userService.aggregateUsersByMonthAndType();
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.RESOURCE_FOUND, aggregatedUsers));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(e.getMessage(), null));
        }
    }

    @GetMapping(UrlMapping.AGGREGATE_BY_STATUS)
    public ResponseEntity<APIResponse> getAggregatedUsersByEnabledStatus() {
        try {
            Map<String, Map<String, Long>> aggregatedData = userService.aggregateUsersByEnabledStatusAndType();
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.RESOURCE_FOUND, aggregatedData));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(e.getMessage(), null));
        }
    }

    @PutMapping(UrlMapping.LOCK_USER_ACCOUNT)
    public ResponseEntity<APIResponse> lockUserAccount(@PathVariable Long userId) {
        try {
            userService.lockUserAccount(userId);
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.LOCKED_ACCOUNT_SUCCESS, null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(e.getMessage(), null));
        }
    }


    @PutMapping(UrlMapping.UNLOCK_USER_ACCOUNT)
    public ResponseEntity<APIResponse> unLockUserAccount(@PathVariable Long userId) {
        try {
            userService.unLockUserAccount(userId);
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.UNLOCKED_ACCOUNT_SUCCESS, null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(e.getMessage(), null));
        }
    }


    @PutMapping(UrlMapping.CHANGE_PASSWORD)
    public ResponseEntity<APIResponse> changePassword(@PathVariable Long userId,
                                                      @RequestBody ChangePasswordRequest request) {
        try {
            changePasswordService.changePassword(userId, request);
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.PASSWORD_CHANGE_SUCCESS, null));
        } catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(new APIResponse(e.getMessage(), null));
        }catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new APIResponse(e.getMessage(), null));
        }catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(e.getMessage(), null));
        }
    }

}
