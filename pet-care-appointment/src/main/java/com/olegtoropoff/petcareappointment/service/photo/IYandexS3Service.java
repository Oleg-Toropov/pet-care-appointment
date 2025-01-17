package com.olegtoropoff.petcareappointment.service.photo;

import java.io.InputStream;

/**
 * Interface for managing interactions with Yandex S3.
 * Provides abstraction for common operations like uploading and deleting files in an S3 bucket.
 */
public interface IYandexS3Service {
    /**
     * Uploads a file to a specified bucket in Yandex S3.
     *
     * @param bucketName   the name of the bucket
     * @param key          the unique key for the file in the bucket
     * @param inputStream  the {@link InputStream} containing the file data
     * @param contentLength the size of the file in bytes
     * @param contentType   the MIME type of the file
     * @return the public URL of the uploaded file
     */
    String uploadFile(String bucketName, String key, InputStream inputStream, long contentLength, String contentType);

    /**
     * Deletes a file from a specified bucket in Yandex S3.
     *
     * @param bucketName the name of the bucket
     * @param key        the unique key of the file in the bucket
     */
    void deleteFile(String bucketName, String key);
}