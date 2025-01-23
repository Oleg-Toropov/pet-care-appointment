package com.olegtoropoff.petcareappointment.yandexs3;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import org.springframework.beans.factory.annotation.Value;

import java.net.URI;

/**
 * Configuration class for setting up the Yandex S3 client.
 * <p>
 * This class is responsible for creating and configuring an instance of the {@link S3Client}
 * to interact with the Yandex Object Storage service. It uses credentials and endpoint
 * details defined in the application's properties file.
 */
@Configuration
public class YandexS3Config {

    /**
     * Access key for Yandex S3 authentication.
     * Loaded from the property: {@code cloud.aws.credentials.access-key}.
     */
    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    /**
     * Secret key for Yandex S3 authentication.
     * Loaded from the property: {@code cloud.aws.credentials.secret-key}.
     */
    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    /**
     * Endpoint for Yandex S3 service.
     * Loaded from the property: {@code cloud.aws.s3.endpoint}.
     */
    @Value("${cloud.aws.s3.endpoint}")
    private String endpoint;

    /**
     * Region for Yandex S3 service.
     * Loaded from the property: {@code cloud.aws.s3.region}.
     */
    @Value("${cloud.aws.s3.region}")
    private String region;

    /**
     * Creates and configures the {@link S3Client} for interacting with Yandex S3.
     *
     * <p>The client is configured with the following:
     * <ul>
     *   <li>Region: defined in {@code cloud.aws.s3.region}</li>
     *   <li>Endpoint: defined in {@code cloud.aws.s3.endpoint}</li>
     *   <li>Credentials: access key and secret key from the application properties</li>
     * </ul>
     *
     * @return an instance of {@link S3Client} configured for Yandex S3.
     */
    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(region))
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }
}
