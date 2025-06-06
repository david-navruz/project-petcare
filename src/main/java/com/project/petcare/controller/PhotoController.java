package com.project.petcare.controller;

import com.project.petcare.exception.ResourceNotFoundException;
import com.project.petcare.model.Photo;
import com.project.petcare.response.APIResponse;
import com.project.petcare.service.photo.IPhotoService;
import com.project.petcare.utils.FeedBackMessage;
import com.project.petcare.utils.UrlMapping;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.processing.SQL;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RequiredArgsConstructor
@RequestMapping(UrlMapping.PHOTOS)
@RestController
public class PhotoController {

    private final IPhotoService photoService;


    @PostMapping(UrlMapping.UPLOAD_PHOTO)
    public ResponseEntity<APIResponse> savePhoto(
            @RequestParam MultipartFile file,
            @RequestParam Long userId) throws IOException, SQLException {

        try {
            Photo photo = photoService.savePhoto(file, userId);
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.PHOTO_UPDATE_SUCCESS, photo));
        } catch (IOException | SQLException e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(e.getMessage(), null));
        }
    }


    // We return the image bytes in the JSON message
    // Generally, it is OK to use byte[] in JSON for small images + API-based apps
    @GetMapping(value = UrlMapping.GET_PHOTO_BY_ID)
    public ResponseEntity<APIResponse> getPhotoById(@PathVariable Long photoId) {
        try {
            Photo photo = photoService.getPhotoById(photoId);
            if (photo != null) {
                byte[] photoBytes = photoService.getPhotoData(photo.getId());
                return ResponseEntity.ok(new APIResponse(FeedBackMessage.PHOTO_UPDATE_SUCCESS, photoBytes));
            }
        } catch (ResourceNotFoundException | SQLException e) {
            return ResponseEntity.status(NOT_FOUND).body(new APIResponse(e.getMessage(), null));
        }
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(null, NOT_FOUND));
    }


    @PutMapping(UrlMapping.UPLOAD_PHOTO)
    public ResponseEntity<APIResponse> updatePhoto(
            @PathVariable Long photoId,
            @RequestBody MultipartFile file) {

        try {
            Photo updatedPhoto = photoService.updatePhoto(photoId, file);
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.PHOTO_UPDATE_SUCCESS, updatedPhoto));

        } catch (ResourceNotFoundException | SQLException | IOException e) {
            return ResponseEntity.status(NOT_FOUND).body(new APIResponse(e.getMessage(), null));
        }
    }


    @DeleteMapping(UrlMapping.DELETE_PHOTO)
    public ResponseEntity<APIResponse> deletePhoto(
            @PathVariable Long photoId,
            @PathVariable Long userId) {

        try {
            Photo photo = photoService.getPhotoById(photoId);
            if (photo != null) {
                photoService.deletePhotoById(photo.getId(), userId);
                return ResponseEntity.ok(new APIResponse(FeedBackMessage.PHOTO_REMOVE_SUCCESS, photo.getId()));
            }
        } catch (ResourceNotFoundException | SQLException e) {
            return ResponseEntity.status(NOT_FOUND).body(new APIResponse(e.getMessage(), null));
        }
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(null, INTERNAL_SERVER_ERROR));
    }


}
