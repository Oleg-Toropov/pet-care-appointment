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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Service class for handling photo-related operations.
 * This class provides methods to save, update, retrieve, and delete photos.
 * It also manages interactions with the database and the Yandex S3 storage.
 */
@Service
@RequiredArgsConstructor
public class PhotoService implements IPhotoService {

    /**
     * The name of the S3 bucket used for storing photos.
     */
    private final String BUCKET_NAME = "bucket-pet-care-appointment";

    private final PhotoRepository photoRepository;
    private final UserRepository userRepository;
    private final IYandexS3Service yandexS3Service;

    /**
     * Saves a photo to the database and uploads it to Yandex S3.
     *
     * @param file   the photo file to be saved
     * @param userId the ID of the user associated with the photo
     * @return the ID of the saved photo
     * @throws IOException if an I/O error occurs while reading the file
     * @throws ResourceNotFoundException if the user with the specified ID is not found
     */
    @Override
    public Long savePhoto(MultipartFile file, Long userId) throws IOException {
        File tempFile = convertMultipartFileToFile(file);

        try (FileInputStream fileInputStream = new FileInputStream(tempFile)) {
            String s3Url = yandexS3Service.uploadFile(
                    BUCKET_NAME,
                    userId + "/" + System.currentTimeMillis(),
                    fileInputStream,
                    tempFile.length(),
                    file.getContentType()
            );

            Photo photo = new Photo();
            photo.setS3Url(s3Url);
            photo.setFileType(file.getContentType());
            photo.setFileName(file.getOriginalFilename());

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException(FeedBackMessage.USER_NOT_FOUND));

            photo.setUser(user);
            Photo savedPhoto = photoRepository.save(photo);

            user.setPhoto(savedPhoto);
            userRepository.save(user);

            return savedPhoto.getId();
        } finally {
            if (tempFile.exists() && !tempFile.delete()) {
                System.err.println("Failed to delete temporary file: " + tempFile.getAbsolutePath());
            }
        }
    }

    /**
     * Retrieves the URL of a photo by its ID.
     *
     * @param id the ID of the photo
     * @return the S3 URL of the photo
     * @throws ResourceNotFoundException if the photo with the specified ID is not found
     */
    @Override
    public String getPhotoUrlById(Long id) {
        Photo photo = photoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(FeedBackMessage.RESOURCE_NOT_FOUND));
        return photo.getS3Url();
    }

    /**
     * Deletes a photo and its association with the user.
     * The photo is also removed from Yandex S3.
     *
     * @param id     the ID of the photo to delete
     * @param userId the ID of the user associated with the photo
     * @return the ID of the deleted photo
     * @throws ResourceNotFoundException if the photo or user with the specified ID is not found
     */
    @Transactional
    @Override
    public Long deletePhoto(Long id, Long userId) {
        Photo photo = photoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(FeedBackMessage.RESOURCE_NOT_FOUND));

        String s3Key = photo.getS3Url().substring(photo.getS3Url().lastIndexOf("/") + 1);
        yandexS3Service.deleteFile(BUCKET_NAME, s3Key);

        photoRepository.delete(photo);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(FeedBackMessage.USER_NOT_FOUND));
        user.setPhoto(null);
        userRepository.save(user);

        return id;
    }

    /**
     * Updates an existing photo by replacing it with a new one.
     * The old photo is removed from Yandex S3.
     *
     * @param id   the ID of the photo to update
     * @param file the new photo file
     * @return the ID of the updated photo
     * @throws IOException if an I/O error occurs while reading the file
     * @throws ResourceNotFoundException if the photo with the specified ID is not found
     */
    @Override
    public Long updatePhoto(Long id, MultipartFile file) throws IOException {
        Photo photo = photoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(FeedBackMessage.RESOURCE_NOT_FOUND));

        String oldS3Key = photo.getS3Url().substring(photo.getS3Url().lastIndexOf("/") + 1);
        yandexS3Service.deleteFile(BUCKET_NAME, oldS3Key);

        File tempFile = convertMultipartFileToFile(file);

        try (FileInputStream fileInputStream = new FileInputStream(tempFile)) {
            String newS3Url = yandexS3Service.uploadFile(
                    BUCKET_NAME,
                    photo.getUser().getId() + "/" + System.currentTimeMillis(),
                    fileInputStream,
                    tempFile.length(),
                    file.getContentType()
            );

            photo.setS3Url(newS3Url);
            photo.setFileType(file.getContentType());
            photo.setFileName(file.getOriginalFilename());
            photoRepository.save(photo);

            return photo.getId();
        } finally {
            if (tempFile.exists() && !tempFile.delete()) {
                System.err.println("Failed to delete temporary file: " + tempFile.getAbsolutePath());
            }
        }
    }

    /**
     * Converts a {@link MultipartFile} to a {@link File}.
     *
     * @param file the {@link MultipartFile} to convert
     * @return the converted {@link File}
     * @throws IOException if an error occurs during the conversion
     */
    public File convertMultipartFileToFile(MultipartFile file) throws IOException {
        File convFile = File.createTempFile("temp", null);
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        }
        return convFile;
    }
}
