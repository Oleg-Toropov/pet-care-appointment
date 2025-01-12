package com.olegtoropoff.petcareappointment.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import java.util.Collection;
import java.util.HashSet;

/**
 * Represents a role in the system.
 * <p>
 * A role defines a specific set of permissions or responsibilities that can be assigned to {@link User} entities.
 * Examples of roles include "ADMIN", "VET", and "PATIENT".
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Role {

    /**
     * Unique identifier for the role.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the role.
     * <p>
     * This is a unique and natural identifier for the role, such as "ADMIN", "VET", or "PATIENT".
     */
    @NaturalId
    private String name;

    /**
     * Collection of users associated with this role.
     * <p>
     * This establishes a many-to-many relationship between {@link User} and {@link Role}.
     */
    @ManyToMany(mappedBy = "roles")
    private Collection<User> users = new HashSet<>();

    /**
     * Constructs a new {@code Role} with the specified name.
     *
     * @param name the name of the role
     */
    public Role(String name) {
        this.name = name;
    }

    /**
     * Gets the name of the role.
     *
     * @return the name of the role, or an empty string if the name is null
     */
    public String getName() {
        return (name != null) ? name : "";
    }

    /**
     * Returns a string representation of the role.
     *
     * @return the name of the role
     */
    @Override
    public String toString() {
        return name;
    }
}
