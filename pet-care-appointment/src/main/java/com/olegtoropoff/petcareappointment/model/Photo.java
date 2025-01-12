package com.olegtoropoff.petcareappointment.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Blob;

/**
 * Represents a photo entity used to store image data associated with users or other entities.
 * <p>
 * The photo includes metadata such as file type and name, along with the binary image data stored as a {@link Blob}.
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
     * The binary image data stored as a {@link Blob}.
     * <p>
     * This contains the actual image content.
     */
    @Lob
    private Blob image;
}
