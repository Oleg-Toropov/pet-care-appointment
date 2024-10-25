package com.olegtoropoff.petcareappointment.service.password;

import com.olegtoropoff.petcareappointment.exception.ResourceNotFoundException;
import com.olegtoropoff.petcareappointment.model.User;
import com.olegtoropoff.petcareappointment.repository.UserRepository;
import com.olegtoropoff.petcareappointment.request.ChangePasswordRequest;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ChangePasswordService implements IChangePasswordService {
    private final UserRepository userRepository;

    @Override
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(FeedBackMessage.USER_NOT_FOUND));
        if (!Objects.equals(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException(FeedBackMessage.CURRENT_PASSWORD_WRONG);
        }

        if (request.getCurrentPassword().isEmpty()  || request.getNewPassword().isEmpty()) {
            throw new IllegalArgumentException(FeedBackMessage.ALL_FIELDS_REQUIRED);
        }

        if (Objects.equals(request.getCurrentPassword(), request.getNewPassword())) {
            throw new IllegalArgumentException(FeedBackMessage.NEW_PASSWORD_MUST_DIFFER);
        }

        if (!Objects.equals(request.getNewPassword(), request.getConfirmNewPassword())) {
            throw new IllegalArgumentException(FeedBackMessage.PASSWORDS_MUST_MATCH);
        }
        user.setPassword(request.getNewPassword());
        userRepository.save(user);
    }
}
