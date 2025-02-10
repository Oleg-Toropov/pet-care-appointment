package com.olegtoropoff.petcareappointment.controller;

import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import com.olegtoropoff.petcareappointment.yandexs3.YandexS3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static com.olegtoropoff.petcareappointment.utils.UrlMapping.*;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@Tag("integration")
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "/clean_database.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "/test_photo_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class PhotoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private YandexS3Service yandexS3Service;

    @BeforeEach
    void setUp() {
        Mockito.when(yandexS3Service.uploadFile(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(),
                Mockito.anyLong(),
                Mockito.anyString()
        )).thenReturn("https://storage.yandexcloud.net/bucket-pet-care-appointment/2/test-key.png");
    }

    @Test
    void testSavePhoto_ValidRequest_StoresInDB() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "fake image data".getBytes()
        );

        mockMvc.perform(multipart(PHOTOS + UPLOAD_PHOTO)
                        .file(mockFile)
                        .param("userId", "4")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(FeedBackMessage.PHOTO_UPDATE_SUCCESS))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void testDeletePhoto_ValidRequest_ReturnsSuccess() throws Exception {
        Mockito.doNothing().when(yandexS3Service).deleteFile(Mockito.anyString(), Mockito.anyString());

        mockMvc.perform(delete(PHOTOS + DELETE_PHOTO, 3L, 3L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(FeedBackMessage.PHOTO_REMOVE_SUCCESS))
                .andExpect(jsonPath("$.data").value(3L));
    }

    @Test
    void testDeletePhoto_PhotoNotFound_ReturnsNotFound() throws Exception {
        mockMvc.perform(delete(PHOTOS + DELETE_PHOTO, 100L, 5L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(FeedBackMessage.RESOURCE_NOT_FOUND))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }

    @Test
    void testDeletePhoto_UserNotFound_ReturnsNotFound() throws Exception {
        mockMvc.perform(delete(PHOTOS + DELETE_PHOTO, 3L, 100L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(FeedBackMessage.USER_NOT_FOUND))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }

    @Test
    void testDeletePhoto_S3DeletionFails_ReturnsServerError() throws Exception {
        Mockito.doThrow(new RuntimeException("S3 Deletion Failed")).when(yandexS3Service)
                .deleteFile(Mockito.anyString(), Mockito.anyString());

        mockMvc.perform(delete(PHOTOS + DELETE_PHOTO, 3L, 3L))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value(FeedBackMessage.ERROR))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }

    @Test
    void testUpdatePhoto_ValidRequest_ReturnsSuccess() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "updated.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "some updated image data".getBytes()
        );

        Mockito.doNothing().when(yandexS3Service).deleteFile(Mockito.anyString(), Mockito.anyString());

        mockMvc.perform(
                        multipart(PHOTOS + UPDATE_PHOTO, 2L)
                                .file(mockFile)
                                .with(request -> {
                                    request.setMethod("PUT");
                                    return request;
                                })
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(FeedBackMessage.PHOTO_UPDATE_SUCCESS))
                .andExpect(jsonPath("$.data").value(2L));
    }

    @Test
    void testUpdatePhoto_NotFound() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "updated.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "some updated image data".getBytes()
        );

        mockMvc.perform(
                        multipart(PHOTOS + UPDATE_PHOTO, 100L)
                                .file(mockFile)
                                .with(request -> {
                                    request.setMethod("PUT");
                                    return request;
                                })
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(FeedBackMessage.RESOURCE_NOT_FOUND))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }

    @Test
    void testUpdatePhoto_S3DeletionFails_ReturnsServerError() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "updated.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "some updated image data".getBytes()
        );

        Mockito.doThrow(new RuntimeException("S3 Deletion Failed")).when(yandexS3Service)
                .deleteFile(Mockito.anyString(), Mockito.anyString());

        mockMvc.perform(
                        multipart(PHOTOS + UPDATE_PHOTO, 2L)
                                .file(mockFile)
                                .with(request -> {
                                    request.setMethod("PUT");
                                    return request;
                                })
                )
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value(FeedBackMessage.ERROR))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }
}
