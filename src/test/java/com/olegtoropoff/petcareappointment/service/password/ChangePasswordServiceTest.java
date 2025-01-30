package com.olegtoropoff.petcareappointment.service.password;

import com.olegtoropoff.petcareappointment.exception.ResourceNotFoundException;
import com.olegtoropoff.petcareappointment.model.User;
import com.olegtoropoff.petcareappointment.repository.UserRepository;
import com.olegtoropoff.petcareappointment.request.ChangePasswordRequest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
class ChangePasswordServiceTest {

    @InjectMocks
    private ChangePasswordService changePasswordService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void changePassword_WhenUserNotFound_ThrowsResourceNotFoundException() {
        Long userId = 1L;
        ChangePasswordRequest request = new ChangePasswordRequest();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> changePasswordService.changePassword(userId, request));

        verify(userRepository, times(1)).findById(userId);
        verifyNoMoreInteractions(userRepository, passwordEncoder);
    }

    @Test
    void changePassword_WhenCurrentPasswordEmpty_ThrowsIllegalArgumentException() {
        Long userId = 1L;
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("");
        request.setNewPassword("NewPass123!");
        request.setConfirmNewPassword("NewPass123!");

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));

        assertThrows(IllegalArgumentException.class,
                () -> changePasswordService.changePassword(userId, request));

        verify(userRepository, times(1)).findById(userId);
        verifyNoMoreInteractions(userRepository, passwordEncoder);
    }

    @Test
    void changePassword_WhenNewPasswordEmpty_ThrowsIllegalArgumentException() {
        Long userId = 1L;
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("OldPass123!");
        request.setNewPassword("");
        request.setConfirmNewPassword("");

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));

        assertThrows(IllegalArgumentException.class,
                () -> changePasswordService.changePassword(userId, request));

        verify(userRepository, times(1)).findById(userId);
        verifyNoMoreInteractions(userRepository, passwordEncoder);
    }

    @Test
    void changePassword_WhenCurrentPasswordWrong_ThrowsIllegalArgumentException() {
        Long userId = 1L;
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("WrongPass123!");
        request.setNewPassword("NewPass123!");
        request.setConfirmNewPassword("NewPass123!");

        User user = new User();
        user.setPassword("EncodedPass");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> changePasswordService.changePassword(userId, request));

        verify(userRepository, times(1)).findById(userId);
        verify(passwordEncoder, times(1)).matches(request.getCurrentPassword(), user.getPassword());
    }

    @Test
    void changePassword_WhenNewPasswordSameAsCurrent_ThrowsIllegalArgumentException() {
        Long userId = 1L;
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("OldPass123!");
        request.setNewPassword("OldPass123!");
        request.setConfirmNewPassword("OldPass123!");

        User user = new User();
        user.setPassword("EncodedPass");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> changePasswordService.changePassword(userId, request));

        verify(userRepository, times(1)).findById(userId);
        verify(passwordEncoder, times(1)).matches(request.getCurrentPassword(), user.getPassword());
    }

    @Test
    void changePassword_WhenNewPasswordInvalid_ThrowsIllegalArgumentException() {
        Long userId = 1L;
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("OldPass123!");
        request.setNewPassword("invalid");
        request.setConfirmNewPassword("invalid");

        User user = new User();
        user.setPassword("EncodedPass");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> changePasswordService.changePassword(userId, request));

        verify(userRepository, times(1)).findById(userId);
        verify(passwordEncoder, times(1)).matches(request.getCurrentPassword(), user.getPassword());
    }

    @Test
    void changePassword_WhenPasswordsMismatch_ThrowsIllegalArgumentException() {
        Long userId = 1L;
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("OldPass123!");
        request.setNewPassword("NewPass123!");
        request.setConfirmNewPassword("MismatchPass123!");

        User user = new User();
        user.setPassword("EncodedPass");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> changePasswordService.changePassword(userId, request));

        verify(userRepository, times(1)).findById(userId);
        verify(passwordEncoder, times(1)).matches(request.getCurrentPassword(), user.getPassword());
    }

    @Test
    void changePassword_WhenValid_SavesNewPassword() {
        Long userId = 1L;
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("OldPass123!");
        request.setNewPassword("NewPass123!");
        request.setConfirmNewPassword("NewPass123!");

        User user = new User();
        user.setPassword("EncodedOldPass");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("OldPass123!", "EncodedOldPass")).thenReturn(true);
        when(passwordEncoder.encode("NewPass123!")).thenReturn("EncodedNewPass");

        changePasswordService.changePassword(userId, request);

        verify(userRepository, times(1)).findById(userId);
        verify(passwordEncoder, times(1)).matches("OldPass123!", "EncodedOldPass");
        verify(passwordEncoder, times(1)).encode("NewPass123!");
        verify(userRepository, times(1)).save(user);

        assertEquals("EncodedNewPass", user.getPassword());
    }
}
