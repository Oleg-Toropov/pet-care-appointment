package com.olegtoropoff.petcareappointment.service.photo;

import com.olegtoropoff.petcareappointment.model.Photo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;

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
     * @throws SQLException if a database error occurs while saving the photo.
     */
    Long savePhoto(MultipartFile file, Long userId) throws IOException, SQLException;

    /**
     * Retrieves a photo by its ID.
     *
     * @param id the ID of the photo to retrieve.
     * @return the {@link Photo} entity.
     */
    Photo getPhotoById(Long id);

    /**
     * Deletes a photo and removes its association with a user.
     *
     * @param id     the ID of the photo to delete.
     * @param userId the ID of the user associated with the photo.
     * @return the ID of the deleted photo.
     * @throws SQLException if a database error occurs during the operation.
     */
    Long deletePhoto(Long id, Long userId) throws SQLException;

    /**
     * Updates an existing photo with new data.
     *
     * @param id   the ID of the photo to update.
     * @param file the new photo file.
     * @return the ID of the updated photo.
     * @throws IOException if an I/O error occurs while reading the file.
     * @throws SQLException if a database error occurs while saving the photo.
     */
    Long updatePhoto(Long id, MultipartFile file) throws SQLException, IOException;

    /**
     * Retrieves the image data for a photo.
     *
     * @param id the ID of the photo.
     * @return a byte array containing the image data.
     * @throws SQLException if a database error occurs while retrieving the photo data.
     */
    byte[] getImageData(Long id) throws SQLException;
}
