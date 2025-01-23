package com.olegtoropoff.petcareappointment.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Represents a user in the Pet Care Appointment system.
 * <p>
 * A user can be of different types (e.g., Admin, Veterinarian, Patient) and may have associated roles, appointments,
 * reviews, and other attributes. The user entity is stored in the database and includes properties for authentication,
 * authorization, and profile management.
 * <p>
 * This class is part of the persistence layer and is annotated with JPA annotations for ORM mapping.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "\"user\"")
public class User {

    /**
     * Unique identifier for the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The first name of the user.
     */
    private String firstName;

    /**
     * The last name of the user.
     */
    private String lastName;

    /**
     * The gender of the user.
     */
    private String gender;

    /**
     * The mobile phone number of the user.
     */
    @Column(name = "mobile")
    private String phoneNumber;

    /**
     * The email address of the user, used for login and communication.
     */
    private String email;

    /**
     * The hashed password of the user for authentication purposes.
     */
    private String password;

    /**
     * The type of user (e.g., "ADMIN", "VET", "PATIENT").
     */
    private String userType;

    /**
     * Indicates whether the user account is enabled.
     */
    private boolean isEnabled;

    /**
     * The date when the user was created.
     * Automatically set when the user is first persisted.
     */
    @CreationTimestamp
    private LocalDate createdAt;

    /**
     * The specialization of the user, typically applicable to veterinarians.
     * This field is transient and not persisted in the database.
     */
    @Transient
    private String specialization;

    /**
     * A list of appointments associated with the user.
     * This field is transient and not persisted in the database.
     */
    @Transient
    private List<Appointment> appointments = new ArrayList<>();

    /**
     * A list of reviews associated with the user.
     * This field is transient and not persisted in the database.
     */
    @Transient
    private List<Review> reviews = new ArrayList<>();

    /**
     * The profile photo of the user.
     * Managed as a one-to-one relationship with cascading operations.
     */
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Photo photo;

    /**
     * Roles assigned to the user for authorization purposes.
     * Managed as a many-to-many relationship.
     */
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Collection<Role> roles = new HashSet<>();

    /**
     * A list of verification tokens associated with the user, used for account confirmation or recovery.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<VerificationToken> verificationTokens = new ArrayList<>();
}
