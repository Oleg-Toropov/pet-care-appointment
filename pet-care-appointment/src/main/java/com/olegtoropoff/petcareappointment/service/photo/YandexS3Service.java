package com.olegtoropoff.petcareappointment.service.photo;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;

/**
 * Service class for managing interactions with Yandex S3.
 * This class provides methods to upload, and delete files in an S3 bucket.
 * It abstracts the operations of the S3 client for easier integration into the application.
 */
@Service
public class YandexS3Service implements IYandexS3Service {
    private final S3Client s3Client;

    /**
     * Constructs a new instance of {@link YandexS3Service}.
     *
     * @param s3Client the {@link S3Client} used to interact with Yandex S3
     */
    public YandexS3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    /**
     * Uploads a file to a specified bucket in Yandex S3.
     *
     * @param bucketName    the name of the bucket
     * @param key           the unique key for the file in the bucket
     * @param inputStream   the {@link InputStream} containing the file data
     * @param contentLength the size of the file in bytes
     * @param contentType   the MIME type of the file
     * @return the public URL of the uploaded file
     * @throws RuntimeException if an error occurs during the upload process
     */
    @Override
    public String uploadFile(String bucketName, String key, InputStream inputStream, long contentLength, String contentType) {
        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(contentType)
                    .contentLength(contentLength)
                    .build();

            s3Client.putObject(request, RequestBody.fromInputStream(inputStream, contentLength));
            return String.format("https://storage.yandexcloud.net/%s/%s", bucketName, key);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    /**
     * Deletes a file from a specified bucket in Yandex S3.
     *
     * @param bucketName the name of the bucket
     * @param key        the unique key of the file in the bucket
     */
    @Override
    public void deleteFile(String bucketName, String key) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build());
    }
}
