package com.olegtoropoff.petcareappointment.controller;

import com.olegtoropoff.petcareappointment.exception.ResourceNotFoundException;
import com.olegtoropoff.petcareappointment.model.Photo;
import com.olegtoropoff.petcareappointment.response.CustomApiResponse;
import com.olegtoropoff.petcareappointment.service.photo.IPhotoService;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

@Tag("unit")
public class PhotoControllerTest {

    @InjectMocks
    private PhotoController photoController;

    @Mock
    private IPhotoService photoService;

    @Mock
    private MultipartFile fileMock;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    public void savePhoto_WhenValidRequest_ReturnsSuccess() throws IOException, SQLException {
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
    public void savePhoto_WhenIOExceptionOccurs_ReturnsInternalServerError() throws IOException, SQLException {
        Long userId = 2L;
        when(photoService.savePhoto(fileMock, userId)).thenThrow(new IOException());

        ResponseEntity<CustomApiResponse> response = photoController.savePhoto(fileMock, userId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(FeedBackMessage.ERROR, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
        verify(photoService, times(1)).savePhoto(fileMock, userId);
    }

    @Test
    public void getPhotoById_WhenPhotoExists_ReturnsPhotoUrlBytes() throws SQLException {
        Long photoId = 2L;
        Photo photo = new Photo();
        photo.setId(photoId);

        byte[] bytes = "image-bytes".getBytes();

        when(photoService.getPhotoById(photoId)).thenReturn(photo);
        when(photoService.getImageData(photoId)).thenReturn(bytes);

        ResponseEntity<CustomApiResponse> response = photoController.getPhotoUrlById(photoId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(FeedBackMessage.RESOURCE_FOUND, Objects.requireNonNull(response.getBody()).getMessage());
        assertArrayEquals(bytes, (byte[]) response.getBody().getData());
        verify(photoService, times(1)).getPhotoById(photoId);
        verify(photoService, times(1)).getImageData(photoId);
    }

    @Test
    public void getPhotoUrlById_WhenNotFound_ReturnsNotFound() {
        Long photoId = 100L;
        when(photoService.getPhotoById(photoId)).thenThrow(new ResourceNotFoundException(FeedBackMessage.RESOURCE_NOT_FOUND));

        ResponseEntity<CustomApiResponse> response = photoController.getPhotoUrlById(photoId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(FeedBackMessage.RESOURCE_NOT_FOUND, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
        verify(photoService, times(1)).getPhotoById(photoId);
    }

    @Test
    public void getPhotoUrlById_WhenAnyOtherExceptionOccurs_ReturnsInternalServerError() {
        Long photoId = 2L;
        when(photoService.getPhotoById(photoId)).thenThrow(new RuntimeException());

        ResponseEntity<CustomApiResponse> response = photoController.getPhotoUrlById(photoId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(FeedBackMessage.ERROR, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
        verify(photoService, times(1)).getPhotoById(photoId);
    }

    @Test
    public void deletePhoto_WhenValidRequest_ReturnsSuccess() throws SQLException {
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
    public void deletePhoto_WhenNotFound_ReturnsNotFound() throws SQLException {
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
    public void deletePhoto_WhenSQLExceptionOccurs_ReturnsNotFound() throws SQLException {
        Long photoId = 999L;
        Long userId = 5L;
        when(photoService.deletePhoto(photoId, userId)).thenThrow(new SQLException("SQL Error"));

        ResponseEntity<CustomApiResponse> response = photoController.deletePhoto(photoId, userId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(FeedBackMessage.RESOURCE_NOT_FOUND, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
        verify(photoService, times(1)).deletePhoto(photoId, userId);
    }

    @Test
    public void deletePhoto_WhenAnyOtherException_ReturnsInternalServerError() throws SQLException {
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
    public void updatePhoto_WhenValidRequest_ReturnsSuccess() throws SQLException, IOException {
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
    public void updatePhoto_WhenNotFound_ReturnsNotFound() throws SQLException, IOException {
        Long photoId = 10L;
        when(photoService.updatePhoto(photoId, fileMock)).thenThrow(new ResourceNotFoundException(FeedBackMessage.RESOURCE_NOT_FOUND));

        ResponseEntity<CustomApiResponse> response = photoController.updatePhoto(photoId, fileMock);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(FeedBackMessage.RESOURCE_NOT_FOUND, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
        verify(photoService, times(1)).updatePhoto(photoId, fileMock);
    }

    @Test
    public void updatePhoto_WhenAnyOtherExceptionOccurs_ReturnsInternalServerError() throws SQLException, IOException {
        Long photoId = 10L;
        when(photoService.updatePhoto(photoId, fileMock)).thenThrow(new RuntimeException(FeedBackMessage.ERROR));

        ResponseEntity<CustomApiResponse> response = photoController.updatePhoto(photoId, fileMock);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(FeedBackMessage.ERROR, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
        verify(photoService, times(1)).updatePhoto(photoId, fileMock);
    }
}
