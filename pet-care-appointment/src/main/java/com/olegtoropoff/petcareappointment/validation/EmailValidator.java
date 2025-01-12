package com.olegtoropoff.petcareappointment.validation;

import java.util.regex.Pattern;

/**
 * Utility class for validating email addresses against a predefined set of domains.
 * <p>
 * This validator ensures that email addresses adhere to a standard format and belong to
 * one of the supported domains. Common domains like "gmail.com", "yahoo.com", and others are included.
 */
public class EmailValidator {

    /**
     * Regular expression for validating email addresses.
     * <p>
     * The regex is designed to match:
     * <ul>
     *     <li>A local part consisting of alphanumeric characters and certain special characters (e.g., '.', '_', '%', '+', '-').</li>
     *     <li>An '@' symbol separating the local part and the domain.</li>
     *     <li>A domain that belongs to a predefined list of valid domains (e.g., "gmail.com", "yandex.ru").</li>
     * </ul>
     */
    private static final String EMAIL_REGEX =
            "^[a-zA-Z0-9._%+-]+" +           // The local part: allows letters, numbers, and special characters
            "@" +                            // The @ symbol separating the local part and the domain
            "(" +                            // Start of the domain options
            "icloud\\.com|" +
            "me\\.com|" +
            "mac\\.com|" +
            "yahoo\\.com|" +
            "outlook\\.com|" +
            "hotmail\\.com|" +
            "live\\.com|" +
            "msn\\.com|" +
            "gmail\\.com|" +
            "pochta\\.ru|" +
            "cloud\\.ru|" +
            "rambler\\.ru|" +
            "lenta\\.ru|" +
            "autorambler\\.ru|" +
            "myrambler\\.ru|" +
            "ro\\.ru|" +
            "yandex\\.ru|" +
            "ya\\.ru|" +
            "yandex\\.com|" +
            "mail\\.ru|" +
            "bk\\.ru|" +
            "list\\.ru|" +
            "inbox\\.ru" +
            ")$";                            // End of the domain options and end of the string

    /**
     * Compiled {@link Pattern} for the email regular expression.
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    /**
     * Validates an email address against the predefined email pattern.
     *
     * @param email the email address to validate
     * @return {@code true} if the email address is valid and belongs to a supported domain, {@code false} otherwise
     */
    public static boolean isValid(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }
}