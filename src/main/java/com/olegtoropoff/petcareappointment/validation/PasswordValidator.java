package com.olegtoropoff.petcareappointment.validation;


import java.util.regex.Pattern;

/**
 * Utility class for validating passwords.
 * <p>
 * This class provides methods to validate passwords against specific security rules,
 * ensuring they meet minimum requirements for complexity and length.
 */
public class PasswordValidator {

    /**
     * Minimum required length for a valid password.
     */
    private static final int MIN_LENGTH = 8;

    /**
     * Regular expression pattern for validating passwords.
     * <p>
     * The pattern ensures that:
     * <ul>
     *     <li>The password contains at least one lowercase letter.</li>
     *     <li>The password contains at least one uppercase letter.</li>
     *     <li>The password contains at least one digit.</li>
     *     <li>The password is at least {@link #MIN_LENGTH} characters long.</li>
     * </ul>
     */
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{" + MIN_LENGTH + ",}$"
    );

    /**
     * Validates the given password against predefined security rules.
     *
     * @param password the password to validate
     * @return {@code true} if the password is valid, {@code false} otherwise
     */
    public static boolean isValid(String password) {
        if (password == null) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(password.trim()).matches();
    }
}