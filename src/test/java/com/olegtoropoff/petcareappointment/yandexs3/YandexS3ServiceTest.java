package com.olegtoropoff.petcareappointment.yandexs3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Tag("unit")
class YandexS3ServiceTest {
    @Mock
    private S3Client s3Client;

    @InjectMocks
    private YandexS3Service yandexS3Service;

    private static final String BUCKET_NAME = "test-bucket";
    private static final String FILE_KEY = "test-file.jpg";
    private static final String CONTENT_TYPE = "image/jpeg";
    private static final byte[] FILE_CONTENT = "fake image data".getBytes();
    private static final InputStream FILE_STREAM = new ByteArrayInputStream(FILE_CONTENT);
    private static final long FILE_SIZE = FILE_CONTENT.length;

    @BeforeEach
    void setUp() {
        s3Client = mock(S3Client.class);
        yandexS3Service = new YandexS3Service(s3Client);
    }

    @Test
    void uploadFile_Success_ReturnsUrl() {
        doAnswer(invocation -> null)
                .when(s3Client)
                .putObject(any(PutObjectRequest.class), any(RequestBody.class));

        String result = yandexS3Service.uploadFile(BUCKET_NAME, FILE_KEY, FILE_STREAM, FILE_SIZE, CONTENT_TYPE);

        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        String expectedUrl = "https://storage.yandexcloud.net/test-bucket/test-file.jpg";
        assertEquals(expectedUrl, result);
    }

    @Test
    void uploadFile_Failure_ThrowsRuntimeException() {
        doThrow(new RuntimeException("S3 Upload Failed")).when(s3Client)
                .putObject(any(PutObjectRequest.class), any(RequestBody.class));

        assertThrows(RuntimeException.class, () ->
                yandexS3Service.uploadFile(BUCKET_NAME, FILE_KEY, FILE_STREAM, FILE_SIZE, CONTENT_TYPE)
        );

        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void deleteFile_Success_DeletesFile() {
        when(s3Client.deleteObject(any(DeleteObjectRequest.class)))
                .thenReturn(null);

        yandexS3Service.deleteFile(BUCKET_NAME, FILE_KEY);

        verify(s3Client, times(1)).deleteObject(any(DeleteObjectRequest.class));
    }
}
