package com.olegtoropoff.petcareappointment.model;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents an administrator in the system.
 * <p>
 * This entity extends the {@link User} class, adding specific attributes and behavior for system administrators.
 * Administrators have elevated privileges and can manage the system's users, appointments, and other resources.
 */
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@PrimaryKeyJoinColumn(name = "admin_id")
public class Admin extends User {

    /**
     * Unique identifier for the administrator.
     * <p>
     * This field is explicitly defined to override or enhance the {@link User} class's ID field.
     */
    private Long id;
}
