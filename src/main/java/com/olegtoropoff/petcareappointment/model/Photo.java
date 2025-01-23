package com.olegtoropoff.petcareappointment.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a photo entity used to store metadata and reference to the stored image in S3.
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Photo {

    /**
     * Unique identifier for the photo.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The type of the file (e.g., "image/png", "image/jpeg").
     * <p>
     * This helps identify the format of the stored image.
     */
    private String fileType;

    /**
     * The name of the file.
     * <p>
     * This can be used for display or organizational purposes.
     */
    private String fileName;

    /**
     * URL of the image stored in S3.
     * <p>
     * This is used to access the file from Yandex S3.
     */
    private String s3Url;

    @OneToOne(mappedBy = "photo")
    private User user;
}
