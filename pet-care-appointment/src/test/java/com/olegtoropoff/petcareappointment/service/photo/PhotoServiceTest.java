package com.olegtoropoff.petcareappointment.service.photo;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.olegtoropoff.petcareappointment.exception.ResourceNotFoundException;
import com.olegtoropoff.petcareappointment.model.Photo;
import com.olegtoropoff.petcareappointment.model.User;
import com.olegtoropoff.petcareappointment.repository.PhotoRepository;
import com.olegtoropoff.petcareappointment.repository.UserRepository;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Optional;

@Tag("unit")
class PhotoServiceTest {

    @InjectMocks
    private PhotoService photoService;

    @Mock
    private PhotoRepository photoRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MultipartFile mockFile;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void savePhoto_Success() throws IOException, SQLException {
        Long userId = 1L;
        User user = new User();
        Photo savedPhoto = new Photo();
        savedPhoto.setId(10L);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(mockFile.getBytes()).thenReturn(new byte[]{1, 2, 3});
        when(mockFile.getContentType()).thenReturn("image/png");
        when(mockFile.getOriginalFilename()).thenReturn("photo.png");
        when(photoRepository.save(any(Photo.class))).thenReturn(savedPhoto);

        Long result = photoService.savePhoto(mockFile, userId);

        assertEquals(savedPhoto.getId(), result);
        verify(userRepository).findById(userId);
        verify(photoRepository).save(any(Photo.class));
        verify(userRepository).save(user);
    }

    @Test
    void savePhoto_NoFileProvided() throws IOException, SQLException {
        Long userId = 1L;
        User user = new User();
        Photo savedPhoto = new Photo();
        savedPhoto.setId(10L);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(photoRepository.save(any(Photo.class))).thenReturn(savedPhoto);

        Long result = photoService.savePhoto(null, userId);

        assertEquals(savedPhoto.getId(), result);
        verify(userRepository).findById(userId);
        verify(photoRepository).save(any(Photo.class));
        verify(userRepository).save(user);
    }

    @Test
    void getPhotoById_Success() {
        Long photoId = 1L;
        Photo photo = new Photo();
        when(photoRepository.findById(photoId)).thenReturn(Optional.of(photo));

        Photo result = photoService.getPhotoById(photoId);

        assertNotNull(result);
        assertEquals(photo, result);
        verify(photoRepository).findById(photoId);
    }

    @Test
    void getPhotoById_NotFound() {
        Long photoId = 1L;
        when(photoRepository.findById(photoId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> photoService.getPhotoById(photoId));

        assertEquals(FeedBackMessage.RESOURCE_NOT_FOUND, exception.getMessage());
        verify(photoRepository).findById(photoId);
    }

    @Test
    void deletePhoto_Success() {
        Long photoId = 1L;
        Long userId = 2L;
        User user = new User();
        Photo photo = new Photo();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(photoRepository.findById(photoId)).thenReturn(Optional.of(photo));

        Long result = photoService.deletePhoto(photoId, userId);

        assertEquals(photoId, result);
        verify(userRepository).findById(userId);
        verify(photoRepository).findById(photoId);
        verify(photoRepository).delete(photo);
    }

    @Test
    void deletePhoto_UserNotFound() {
        Long photoId = 1L;
        Long userId = 2L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> photoService.deletePhoto(photoId, userId));

        assertEquals(FeedBackMessage.RESOURCE_NOT_FOUND, exception.getMessage());
        verify(userRepository).findById(userId);
        verifyNoInteractions(photoRepository);
    }

    @Test
    void deletePhoto_PhotoNotFound() {
        Long photoId = 1L;
        Long userId = 2L;
        User user = new User();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(photoRepository.findById(photoId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> photoService.deletePhoto(photoId, userId));

        assertEquals(FeedBackMessage.RESOURCE_NOT_FOUND, exception.getMessage());
        verify(userRepository).findById(userId);
        verify(photoRepository).findById(photoId);
    }

    @Test
    void updatePhoto_Success() throws IOException, SQLException {
        Long photoId = 1L;
        Photo photo = new Photo();
        photo.setId(photoId);
        when(photoRepository.findById(photoId)).thenReturn(Optional.of(photo));
        when(mockFile.getBytes()).thenReturn(new byte[]{1, 2, 3});
        when(mockFile.getContentType()).thenReturn("image/png");
        when(mockFile.getOriginalFilename()).thenReturn("updated.png");
        when(photoRepository.save(photo)).thenReturn(photo);

        Long result = photoService.updatePhoto(photoId, mockFile);

        assertEquals(photoId, result);
        assertEquals("image/png", photo.getFileType());
        assertEquals("updated.png", photo.getFileName());
        verify(photoRepository).findById(photoId);
        verify(photoRepository).save(photo);
    }

    @Test
    void updatePhoto_NotFound() {
        Long photoId = 1L;
        when(photoRepository.findById(photoId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> photoService.updatePhoto(photoId, mockFile));

        assertEquals(FeedBackMessage.RESOURCE_NOT_FOUND, exception.getMessage());
        verify(photoRepository).findById(photoId);
    }

    @Test
    void getImageData_Success() throws SQLException {
        Long photoId = 1L;
        Photo photo = new Photo();
        Blob blob = new SerialBlob(new byte[]{1, 2, 3});
        photo.setImage(blob);
        when(photoRepository.findById(photoId)).thenReturn(Optional.of(photo));

        byte[] result = photoService.getImageData(photoId);

        assertArrayEquals(new byte[]{1, 2, 3}, result);
        verify(photoRepository).findById(photoId);
    }

    @Test
    void getImageData_NotFound() {
        Long photoId = 1L;
        when(photoRepository.findById(photoId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> photoService.getImageData(photoId));

        assertEquals(FeedBackMessage.RESOURCE_NOT_FOUND, exception.getMessage());
        verify(photoRepository).findById(photoId);
    }
}
