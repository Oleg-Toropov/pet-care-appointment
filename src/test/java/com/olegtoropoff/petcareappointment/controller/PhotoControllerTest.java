package com.olegtoropoff.petcareappointment.controller;

import com.olegtoropoff.petcareappointment.exception.ResourceNotFoundException;
import com.olegtoropoff.petcareappointment.response.CustomApiResponse;
import com.olegtoropoff.petcareappointment.service.photo.IPhotoService;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
public class PhotoControllerTest {

    @InjectMocks
    private PhotoController photoController;

    @Mock
    private IPhotoService photoService;

    @Mock
    private MultipartFile fileMock;

    @Test
    public void savePhoto_WhenValidRequest_ReturnsSuccess() throws IOException {
        Long userId = 2L;
        Long savedPhotoId = 2L;
        when(photoService.savePhoto(fileMock, userId)).thenReturn(savedPhotoId);

        ResponseEntity<CustomApiResponse> response = photoController.savePhoto(fileMock, userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(FeedBackMessage.PHOTO_UPDATE_SUCCESS, Objects.requireNonNull(response.getBody()).getMessage());
        assertEquals(savedPhotoId, response.getBody().getData());
        verify(photoService, times(1)).savePhoto(fileMock, userId);
    }

    @Test
    public void savePhoto_WhenNotFound_ReturnsNotFound() throws IOException {
        Long userId = 2L;
        when(photoService.savePhoto(fileMock, userId)).thenThrow(new ResourceNotFoundException(FeedBackMessage.USER_NOT_FOUND));

        ResponseEntity<CustomApiResponse> response = photoController.savePhoto(fileMock, userId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(FeedBackMessage.USER_NOT_FOUND, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
        verify(photoService, times(1)).savePhoto(fileMock, userId);
    }

    @Test
    public void savePhoto_WhenIOExceptionOccurs_ReturnsInternalServerError() throws IOException {
        Long userId = 2L;
        when(photoService.savePhoto(fileMock, userId)).thenThrow(new IOException());

        ResponseEntity<CustomApiResponse> response = photoController.savePhoto(fileMock, userId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(FeedBackMessage.ERROR, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
        verify(photoService, times(1)).savePhoto(fileMock, userId);
    }

    @Test
    public void deletePhoto_WhenValidRequest_ReturnsSuccess() {
        Long photoId = 10L;
        Long userId = 5L;
        Long deletedPhotoId = 10L;
        when(photoService.deletePhoto(photoId, userId)).thenReturn(deletedPhotoId);

        ResponseEntity<CustomApiResponse> response = photoController.deletePhoto(photoId, userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(FeedBackMessage.PHOTO_REMOVE_SUCCESS, Objects.requireNonNull(response.getBody()).getMessage());
        assertEquals(deletedPhotoId, response.getBody().getData());
        verify(photoService, times(1)).deletePhoto(photoId, userId);
    }

    @Test
    public void deletePhoto_WhenNotFound_ReturnsNotFound() {
        Long photoId = 100L;
        Long userId = 5L;
        when(photoService.deletePhoto(photoId, userId)).thenThrow(new ResourceNotFoundException(FeedBackMessage.RESOURCE_NOT_FOUND));

        ResponseEntity<CustomApiResponse> response = photoController.deletePhoto(photoId, userId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(FeedBackMessage.RESOURCE_NOT_FOUND, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
        verify(photoService, times(1)).deletePhoto(photoId, userId);
    }

    @Test
    public void deletePhoto_WhenAnyOtherException_ReturnsInternalServerError() {
        Long photoId = 2L;
        Long userId = 5L;
        when(photoService.deletePhoto(photoId, userId)).thenThrow(new RuntimeException(FeedBackMessage.ERROR));

        ResponseEntity<CustomApiResponse> response = photoController.deletePhoto(photoId, userId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(FeedBackMessage.ERROR, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
        verify(photoService, times(1)).deletePhoto(photoId, userId);
    }

    @Test
    public void updatePhoto_WhenValidRequest_ReturnsSuccess() throws IOException {
        Long photoId = 2L;
        Long updatedPhotoId = 3L;
        when(photoService.updatePhoto(photoId, fileMock)).thenReturn(updatedPhotoId);

        ResponseEntity<CustomApiResponse> response = photoController.updatePhoto(photoId, fileMock);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(FeedBackMessage.PHOTO_UPDATE_SUCCESS, Objects.requireNonNull(response.getBody()).getMessage());
        assertEquals(updatedPhotoId, response.getBody().getData());
        verify(photoService, times(1)).updatePhoto(photoId, fileMock);
    }

    @Test
    public void updatePhoto_WhenNotFound_ReturnsNotFound() throws IOException {
        Long photoId = 10L;
        when(photoService.updatePhoto(photoId, fileMock)).thenThrow(new ResourceNotFoundException(FeedBackMessage.RESOURCE_NOT_FOUND));

        ResponseEntity<CustomApiResponse> response = photoController.updatePhoto(photoId, fileMock);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(FeedBackMessage.RESOURCE_NOT_FOUND, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
        verify(photoService, times(1)).updatePhoto(photoId, fileMock);
    }

    @Test
    public void updatePhoto_WhenAnyOtherExceptionOccurs_ReturnsInternalServerError() throws IOException {
        Long photoId = 10L;
        when(photoService.updatePhoto(photoId, fileMock)).thenThrow(new RuntimeException(FeedBackMessage.ERROR));

        ResponseEntity<CustomApiResponse> response = photoController.updatePhoto(photoId, fileMock);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(FeedBackMessage.ERROR, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
        verify(photoService, times(1)).updatePhoto(photoId, fileMock);
    }
}
