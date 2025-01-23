package com.olegtoropoff.petcareappointment.validation;

import java.util.regex.Pattern;

/**
 * Utility class for validating phone numbers.
 * <p>
 * This class provides methods to validate Russian phone numbers against a predefined pattern.
 * Supported formats include numbers with prefixes "+7" or "8" and operator codes ranging from 901 to 999.
 */
public class PhoneValidator {

    /**
     * Regular expression for validating Russian phone numbers.
     * <p>
     * The pattern ensures that:
     * <ul>
     *     <li>The number starts with either "+7" or "8" (optional).</li>
     *     <li>The operator code begins with "9" and supports codes from 901 to 999.</li>
     *     <li>The phone number includes 10 digits (excluding prefix).</li>
     *     <li>Spaces or dashes are allowed between segments of the number.</li>
     * </ul>
     * Example of valid formats:
     * <ul>
     *     <li>+7 901 123 45 67</li>
     *     <li>8 (901) 123-45-67</li>
     *     <li>89011234567</li>
     * </ul>
     */
    private static final String PHONE_REGEX =
            "^" +
            "(\\+7|8)?" +                     // Prefix: "+7" or "8", optional
            "[\\s-]?" +                       // Allows a space or dash after the prefix
            "(\\(?9" +                        // Operator code starts with "9" and can be enclosed in parentheses
            "(" +
            "0[1-9]|" +                       // Codes: 901-909
            "1[0-9]|" +                       // Codes: 910-919
            "2[0-9]|" +                       // Codes: 920-929
            "3[0-9]|" +                       // Codes: 930-939
            "5[0-9]|" +                       // Codes: 950-959
            "6[0-9]|" +                       // Codes: 960-969
            "7[7-8]|" +                       // Codes: 977, 978
            "8[0-9]|" +                       // Codes: 980-989
            "9[0-9]" +                        // Codes: 990-999
            ")" +
            "\\)?)" +
            "[\\s-]?" +                       // Allows a space or dash after the operator code
            "\\d{3}" +                        // Three digits (first part of the phone number)
            "[\\s-]?" +                       // Allows a space or dash
            "\\d{2}" +                        // Two digits (second part of the phone number)
            "[\\s-]?" +                       // Allows a space or dash
            "\\d{2}" +                        // Two digits (third part of the phone number)
            "$";                              // End of the string

    /**
     * Compiled pattern based on the phone number regular expression.
     */
    private static final Pattern PHONE_PATTERN = Pattern.compile(PHONE_REGEX);

    /**
     * Validates the given phone number against the Russian phone number pattern.
     *
     * @param phoneNumber the phone number to validate
     * @return {@code true} if the phone number matches the pattern, {@code false} otherwise
     */
    public static boolean isValid(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phoneNumber).matches();
    }
}
