package com.olegtoropoff.petcareappointment.service.photo;

import com.olegtoropoff.petcareappointment.exception.ResourceNotFoundException;
import com.olegtoropoff.petcareappointment.model.Photo;
import com.olegtoropoff.petcareappointment.model.User;
import com.olegtoropoff.petcareappointment.repository.PhotoRepository;
import com.olegtoropoff.petcareappointment.repository.UserRepository;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import com.olegtoropoff.petcareappointment.yandexs3.IYandexS3Service;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
class PhotoServiceTest {

    @InjectMocks
    private PhotoService photoService;

    @Mock
    private PhotoRepository photoRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private IYandexS3Service yandexS3Service;

    @Mock
    private MultipartFile mockFile;

    @Test
    void savePhoto_Success() throws IOException {
        Long userId = 1L;
        User user = new User();
        Photo savedPhoto = new Photo();
        savedPhoto.setId(10L);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(mockFile.getBytes()).thenReturn(new byte[]{1, 2, 3});
        when(mockFile.getContentType()).thenReturn("image/png");
        when(mockFile.getOriginalFilename()).thenReturn("photo.png");
        when(photoRepository.save(any(Photo.class))).thenReturn(savedPhoto);
        when(yandexS3Service.uploadFile(anyString(), anyString(), any(FileInputStream.class), anyLong(), anyString())).thenReturn("s3-url");

        Long result = photoService.savePhoto(mockFile, userId);

        assertEquals(savedPhoto.getId(), result);
        verify(userRepository).findById(userId);
        verify(photoRepository).save(any(Photo.class));
        verify(userRepository).save(user);
        verify(yandexS3Service).uploadFile(anyString(), anyString(), any(FileInputStream.class), anyLong(), anyString());
    }

    @Test
    void savePhoto_UserNotFound() throws IOException {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        when(mockFile.getBytes()).thenReturn(new byte[]{1, 2, 3});
        when(mockFile.getContentType()).thenReturn("image/png");
        when(mockFile.getOriginalFilename()).thenReturn("photo.png");
        when(yandexS3Service.uploadFile(anyString(), anyString(), any(FileInputStream.class), anyLong(), anyString())).thenReturn("s3-url");

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                photoService.savePhoto(mockFile, userId));

        assertEquals(FeedBackMessage.USER_NOT_FOUND, exception.getMessage());
        verify(userRepository).findById(userId);
        verifyNoInteractions(photoRepository);
    }

    @Test
    void getPhotoUrlById_Success() {
        Long photoId = 1L;
        Photo photo = new Photo();
        photo.setS3Url("s3-url");

        when(photoRepository.findById(photoId)).thenReturn(Optional.of(photo));

        String result = photoService.getPhotoUrlById(photoId);

        assertEquals("s3-url", result);
        verify(photoRepository).findById(photoId);
    }

    @Test
    void getPhotoUrlById_NotFound() {
        Long photoId = 1L;
        when(photoRepository.findById(photoId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> photoService.getPhotoUrlById(photoId));

        assertEquals(FeedBackMessage.RESOURCE_NOT_FOUND, exception.getMessage());
        verify(photoRepository).findById(photoId);
    }

    @Test
    void deletePhoto_Success() {
        Long photoId = 1L;
        Long userId = 2L;
        User user = new User();
        Photo photo = new Photo();
        photo.setS3Url("s3-url");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(photoRepository.findById(photoId)).thenReturn(Optional.of(photo));

        Long result = photoService.deletePhoto(photoId, userId);

        assertEquals(photoId, result);
        verify(userRepository).findById(userId);
        verify(photoRepository).findById(photoId);
        verify(photoRepository).delete(photo);
        verify(yandexS3Service).deleteFile(anyString(), anyString());
    }

    @Test
    void deletePhoto_UserNotFound() {
        Long photoId = 1L;
        Long userId = 2L;

        when(photoRepository.findById(photoId)).thenReturn(Optional.of(new Photo()));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                photoService.deletePhoto(photoId, userId));

        assertEquals(FeedBackMessage.USER_NOT_FOUND, exception.getMessage());
        verify(userRepository).findById(userId);
        verifyNoInteractions(yandexS3Service);
    }

    @Test
    void deletePhoto_PhotoNotFound() {
        Long photoId = 1L;
        Long userId = 2L;

        when(photoRepository.findById(photoId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                photoService.deletePhoto(photoId, userId));

        assertEquals(FeedBackMessage.RESOURCE_NOT_FOUND, exception.getMessage());
        verify(photoRepository).findById(photoId);
        verifyNoInteractions(userRepository, yandexS3Service);
    }

    @Test
    void updatePhoto_Success() throws IOException {
        Long photoId = 1L;
        Photo photo = new Photo();
        photo.setS3Url("old/s3-url");
        photo.setId(photoId);
        User user = new User();
        user.setId(2L);
        photo.setUser(user);

        when(photoRepository.findById(photoId)).thenReturn(Optional.of(photo));
        when(mockFile.getBytes()).thenReturn(new byte[]{1, 2, 3});
        when(mockFile.getContentType()).thenReturn("image/png");
        when(mockFile.getOriginalFilename()).thenReturn("updated.png");

        when(yandexS3Service.uploadFile(anyString(), anyString(), any(FileInputStream.class), anyLong(), anyString()))
                .thenReturn("new-s3-url");

        Long result = photoService.updatePhoto(photoId, mockFile);

        assertEquals(photoId, result);
        assertEquals("new-s3-url", photo.getS3Url());
        verify(photoRepository).findById(photoId);
        verify(photoRepository).save(photo);
        verify(yandexS3Service).deleteFile(anyString(), anyString());
        verify(yandexS3Service).uploadFile(anyString(), anyString(), any(FileInputStream.class), anyLong(), anyString());
    }

    @Test
    void updatePhoto_NotFound() {
        Long photoId = 1L;
        when(photoRepository.findById(photoId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> photoService.updatePhoto(photoId, mockFile));

        assertEquals(FeedBackMessage.RESOURCE_NOT_FOUND, exception.getMessage());
        verify(photoRepository).findById(photoId);
        verifyNoInteractions(yandexS3Service);
    }
}