package com.olegtoropoff.petcareappointment.security.user;

import com.olegtoropoff.petcareappointment.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link UserDetails} for the application.
 * <p>
 * This class is used by Spring Security to represent authenticated user details.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UPCUserDetails implements UserDetails {

    /**
     * Unique identifier of the user.
     */
    private Long id;

    /**
     * Email address of the user. Used as the username for authentication.
     */
    private String email;

    /**
     * Encrypted password of the user.
     */
    private String password;

    /**
     * Indicates whether the user account is enabled.
     */
    private boolean isEnabled;

    /**
     * Collection of authorities granted to the user.
     * Represents the roles or permissions assigned to the user.
     */
    private Collection<GrantedAuthority> authorities;

    /**
     * Builds a UPCUserDetails object from the given User entity.
     *
     * @param user the User entity to map to UserDetails
     * @return a UPCUserDetails instance
     */
    public static UPCUserDetails buildUserDetails(User user) {
        List<GrantedAuthority> authorities = user.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
        return new UPCUserDetails(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.isEnabled(),
                authorities);
    }

    /**
     * Returns the authorities granted to the user.
     *
     * @return a collection of {@link GrantedAuthority}
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * Returns the user's password.
     *
     * @return the password
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Returns the user's email as the username.
     *
     * @return the email address
     */
    @Override
    public String getUsername() {
        return email;
    }

    /**
     * Indicates whether the user's account is expired.
     *
     * @return {@code true} if the account is not expired, otherwise {@code false}
     */
    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    /**
     * Indicates whether the user's account is enabled.
     *
     * @return {@code true} if the account is enabled, otherwise {@code false}
     */
    @Override
    public boolean isEnabled() {
        return isEnabled;
    }
}
