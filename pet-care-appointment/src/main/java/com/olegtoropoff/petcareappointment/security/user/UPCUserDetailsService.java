package com.olegtoropoff.petcareappointment.security.user;

import com.olegtoropoff.petcareappointment.model.User;
import com.olegtoropoff.petcareappointment.repository.UserRepository;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service for loading user-specific data.
 * <p>
 * This implementation of {@link UserDetailsService} retrieves user details from the database
 * using the {@link UserRepository}.
 */
@Service
@RequiredArgsConstructor
public class UPCUserDetailsService implements UserDetailsService {
    private  final UserRepository userRepository;

    /**
     * Loads the user's details by their email.
     *
     * @param email the email identifying the user whose data is required.
     * @return a {@link UserDetails} object containing user information for authentication and authorization.
     * @throws UsernameNotFoundException if the user is not found in the repository.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(FeedBackMessage.RESOURCE_NOT_FOUND));
        return UPCUserDetails.buildUserDetails(user);
    }
}
