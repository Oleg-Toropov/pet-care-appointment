package com.olegtoropoff.petcareappointment.service.photo;

import com.olegtoropoff.petcareappointment.exception.ResourceNotFoundException;
import com.olegtoropoff.petcareappointment.model.Photo;
import com.olegtoropoff.petcareappointment.model.User;
import com.olegtoropoff.petcareappointment.repository.PhotoRepository;
import com.olegtoropoff.petcareappointment.repository.UserRepository;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Service implementation for managing photo-related operations.
 * Handles saving, retrieving, updating, and deleting photo data.
 */
@Service
@RequiredArgsConstructor
public class PhotoService implements IPhotoService {
    private final PhotoRepository photoRepository;
    private final UserRepository userRepository;

    /**
     * Saves a photo file and associates it with a user.
     *
     * @param file   the photo file to be saved.
     * @param userId the ID of the user to associate the photo with.
     * @return the ID of the saved photo.
     * @throws IOException if an I/O error occurs while reading the file.
     * @throws SQLException if a database error occurs while saving the photo.
     */
    @Override
    public Long savePhoto(MultipartFile file, Long userId) throws IOException, SQLException {
        Optional<User> theUser = userRepository.findById(userId);
        Photo photo = new Photo();
        if (file != null && !file.isEmpty()) {
            byte[] photoBytes = file.getBytes();
            Blob photoBlob = new SerialBlob(photoBytes);
            photo.setImage(photoBlob);
            photo.setFileType(file.getContentType());
            photo.setFileName(file.getOriginalFilename());
        }
        Photo savedPhoto = photoRepository.save(photo);
        theUser.ifPresent(user -> {
            user.setPhoto(savedPhoto);
            userRepository.save(theUser.get());
        });
        return savedPhoto.getId();
    }

    /**
     * Retrieves a photo by its ID.
     *
     * @param id the ID of the photo to retrieve.
     * @return the {@link Photo} entity.
     * @throws ResourceNotFoundException if the photo with the given ID is not found.
     */
    @Override
    public Photo getPhotoById(Long id) {
        return photoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(FeedBackMessage.RESOURCE_NOT_FOUND));
    }

    /**
     * Deletes a photo and removes its association with the user.
     *
     * @param id     the ID of the photo to delete.
     * @param userId the ID of the user associated with the photo.
     * @return the ID of the deleted photo.
     * @throws ResourceNotFoundException if the user or photo with the given ID is not found.
     */
    @Transactional
    @Override
    public Long deletePhoto(Long id, Long userId) {
        userRepository.findById(userId)
                .ifPresentOrElse(User::removeUserPhoto, () -> {
                    throw new ResourceNotFoundException(FeedBackMessage.RESOURCE_NOT_FOUND);
                });
        photoRepository.findById(id)
                .ifPresentOrElse(photoRepository::delete, () -> {
                    throw new ResourceNotFoundException(FeedBackMessage.RESOURCE_NOT_FOUND);
                });
        return id;
    }

    /**
     * Updates an existing photo with new data.
     *
     * @param id   the ID of the photo to update.
     * @param file the new photo file.
     * @return the ID of the updated photo.
     * @throws SQLException if a database error occurs while saving the photo.
     * @throws IOException if an I/O error occurs while reading the file.
     */
    @Override
    public Long updatePhoto(Long id, MultipartFile file) throws SQLException, IOException {
        Photo photo = getPhotoById(id);
        byte[] photoBytes = file.getBytes();
        Blob photoBlob = new SerialBlob(photoBytes);
        photo.setImage(photoBlob);
        photo.setFileType(file.getContentType());
        photo.setFileName(file.getOriginalFilename());
        photoRepository.save(photo);
        return photo.getId();
    }

    /**
     * Retrieves the image data for a photo.
     *
     * @param id the ID of the photo.
     * @return the byte array of the image data.
     * @throws SQLException if a database error occurs while retrieving the photo data.
     */
    @Override
    public byte[] getImageData(Long id) throws SQLException {
        Photo photo = getPhotoById(id);
        Blob photoBlob = photo.getImage();
        return photoBlob.getBytes(1, (int) photoBlob.length());
    }
}
