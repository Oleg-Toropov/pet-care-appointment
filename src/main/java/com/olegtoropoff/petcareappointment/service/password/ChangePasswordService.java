package com.olegtoropoff.petcareappointment.service.password;

import com.olegtoropoff.petcareappointment.exception.ResourceNotFoundException;
import com.olegtoropoff.petcareappointment.model.User;
import com.olegtoropoff.petcareappointment.repository.UserRepository;
import com.olegtoropoff.petcareappointment.request.ChangePasswordRequest;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import com.olegtoropoff.petcareappointment.validation.PasswordValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service implementation for handling user password changes.
 * Provides functionality to validate and update a user's password.
 */
@Service
@RequiredArgsConstructor
public class ChangePasswordService implements IChangePasswordService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Changes the password for the user with the given ID.
     *
     * @param userId  the ID of the user whose password is to be changed
     * @param request the {@link ChangePasswordRequest} containing current, new, and confirmation passwords
     * @throws ResourceNotFoundException    if the user with the specified ID is not found
     * @throws IllegalArgumentException     if any validation fails:
     *                                      <ul>
     *                                          <li>The current or new password is empty</li>
     *                                          <li>The current password does not match the stored password</li>
     *                                          <li>The new password matches the current password</li>
     *                                          <li>The new password does not meet the required format</li>
     *                                          <li>The new password and confirmation password do not match</li>
     *                                      </ul>
     */
    @Override
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(FeedBackMessage.USER_NOT_FOUND));
        if (request.getCurrentPassword().isEmpty() || request.getNewPassword().isEmpty()) {
            throw new IllegalArgumentException(FeedBackMessage.EMPTY_PASSWORD);
        }

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException(FeedBackMessage.CURRENT_PASSWORD_WRONG);
        }

        if (request.getCurrentPassword().equals(request.getNewPassword())) {
            throw new IllegalArgumentException(FeedBackMessage.NEW_PASSWORD_MUST_DIFFER);
        }

        if (!PasswordValidator.isValid(request.getNewPassword())) {
            throw new IllegalArgumentException(FeedBackMessage.INVALID_PASSWORD_FORMAT);
        }

        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new IllegalArgumentException(FeedBackMessage.PASSWORD_MISMATCH);
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
