package com.olegtoropoff.petcareappointment.service.photo;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Interface for managing photo-related operations.
 * Provides methods for saving, retrieving, updating, and deleting photos.
 */
public interface IPhotoService {

    /**
     * Saves a photo file and associates it with a user.
     *
     * @param file   the photo file to be saved.
     * @param userId the ID of the user to associate the photo with.
     * @return the ID of the saved photo.
     * @throws IOException if an I/O error occurs while reading the file.
     */
    Long savePhoto(MultipartFile file, Long userId) throws IOException;

    /**
     * Retrieves the URL of a photo by its ID.
     *
     * @param id the ID of the photo to retrieve.
     * @return the URL of the photo.
     */
    String getPhotoUrlById(Long id);

    /**
     * Deletes a photo and removes its association with a user.
     *
     * @param id     the ID of the photo to delete.
     * @param userId the ID of the user associated with the photo.
     * @return the ID of the deleted photo.
     */
    Long deletePhoto(Long id, Long userId);

    /**
     * Updates an existing photo with new data.
     *
     * @param id   the ID of the photo to update.
     * @param file the new photo file.
     * @return the ID of the updated photo.
     * @throws IOException if an I/O error occurs while reading the file.
     */
    Long updatePhoto(Long id, MultipartFile file) throws IOException;
}