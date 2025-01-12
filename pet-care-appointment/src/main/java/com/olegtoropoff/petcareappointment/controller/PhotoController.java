package com.olegtoropoff.petcareappointment.controller;

import com.olegtoropoff.petcareappointment.exception.ResourceNotFoundException;
import com.olegtoropoff.petcareappointment.model.Photo;
import com.olegtoropoff.petcareappointment.response.CustomApiResponse;
import com.olegtoropoff.petcareappointment.service.photo.IPhotoService;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import com.olegtoropoff.petcareappointment.utils.UrlMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;

import static org.springframework.http.HttpStatus.*;

/**
 * This controller handles CRUD operations for user photos.
 */
@RestController
@RequestMapping(UrlMapping.PHOTOS)
@RequiredArgsConstructor
public class PhotoController {
    private final IPhotoService photoService;

    /**
     * Uploads a photo for a specified user.
     *
     * @param file   the photo file to be uploaded
     * @param userId the ID of the user associated with the photo
     * @return a response containing the ID of the saved photo
     */
    @PostMapping(UrlMapping.UPLOAD_PHOTO)
    public ResponseEntity<CustomApiResponse> savePhoto(@RequestParam MultipartFile file,
                                                       @RequestParam Long userId) {
        try {
            Long id = photoService.savePhoto(file, userId);
            return ResponseEntity.ok(new CustomApiResponse(FeedBackMessage.PHOTO_UPDATE_SUCCESS, id));
        } catch (IOException | SQLException e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new CustomApiResponse(FeedBackMessage.ERROR, null));
        }
    }

    /**
     * Retrieves a photo by its ID.
     *
     * @param photoId the ID of the photo to be retrieved
     * @return a response containing the photo data as a byte array
     */
    @GetMapping(value = UrlMapping.GET_PHOTO_BY_ID)
    public ResponseEntity<CustomApiResponse> getPhotoById(@PathVariable Long photoId) {
        try {
            Photo photo = photoService.getPhotoById(photoId);
            byte[] photoBytes = photoService.getImageData(photo.getId());
            return ResponseEntity.ok(new CustomApiResponse(FeedBackMessage.RESOURCE_FOUND, photoBytes));
        } catch (ResourceNotFoundException | SQLException e) {
            return ResponseEntity.status(NOT_FOUND).body(new CustomApiResponse(FeedBackMessage.RESOURCE_NOT_FOUND, null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new CustomApiResponse(FeedBackMessage.ERROR, null));
        }
    }

    /**
     * Deletes a photo by its ID.
     *
     * @param photoId the ID of the photo to be deleted
     * @param userId  the ID of the user associated with the photo
     * @return a response containing the ID of the deleted photo
     */
    @DeleteMapping(UrlMapping.DELETE_PHOTO)
    public ResponseEntity<CustomApiResponse> deletePhoto(@PathVariable Long photoId, @PathVariable Long userId) {
        try {
            Long id = photoService.deletePhoto(photoId, userId);
            return ResponseEntity.ok(new CustomApiResponse(FeedBackMessage.PHOTO_REMOVE_SUCCESS, id));
        } catch (ResourceNotFoundException | SQLException e) {
            return ResponseEntity.status(NOT_FOUND).body(new CustomApiResponse(FeedBackMessage.RESOURCE_NOT_FOUND, null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new CustomApiResponse(FeedBackMessage.ERROR, null));
        }
    }

    /**
     * Updates an existing photo.
     *
     * @param photoId the ID of the photo to be updated
     * @param file    the new photo file to replace the old one
     * @return a response containing the ID of the updated photo
     */
    @PutMapping(UrlMapping.UPDATE_PHOTO)
    public ResponseEntity<CustomApiResponse> updatePhoto(@PathVariable Long photoId, @RequestBody MultipartFile file) {
        try {
            Long id = photoService.updatePhoto(photoId, file);
            return ResponseEntity.ok(new CustomApiResponse(FeedBackMessage.PHOTO_UPDATE_SUCCESS, id));
        } catch (ResourceNotFoundException | SQLException e) {
            return ResponseEntity.status(NOT_FOUND).body(new CustomApiResponse(FeedBackMessage.RESOURCE_NOT_FOUND, null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new CustomApiResponse(FeedBackMessage.ERROR, null));
        }
    }
}
