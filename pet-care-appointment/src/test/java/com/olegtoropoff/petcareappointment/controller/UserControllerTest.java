package com.olegtoropoff.petcareappointment.controller;

import com.olegtoropoff.petcareappointment.exception.ResourceNotFoundException;
import com.olegtoropoff.petcareappointment.service.user.IUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private IUserService userService;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    public void getPhotoByUserId_WhenUserExists_ReturnsPhoto() {
        Long userId = 4L;
        byte[] photoBytes = "fake-image-data".getBytes();
        when(userService.getPhotoByUserId(userId)).thenReturn(photoBytes);

        ResponseEntity<byte[]> response = userController.getPhotoByUserId(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.IMAGE_JPEG, response.getHeaders().getContentType());
        assertEquals(photoBytes, response.getBody());
    }

    @Test
    public void getPhotoByUserId_WhenUserNotFound_ThrowsResourceNotFoundException() {
        Long userId = 100L;
        when(userService.getPhotoByUserId(userId)).thenThrow(new ResourceNotFoundException(""));

        ResponseEntity<byte[]> response = userController.getPhotoByUserId(userId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void getPhotoByUserId_WhenInternalServerError_ThrowsException() {
        Long userId = 4L;
        when(userService.getPhotoByUserId(userId)).thenThrow(new RuntimeException());

        ResponseEntity<byte[]> response = userController.getPhotoByUserId(userId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
