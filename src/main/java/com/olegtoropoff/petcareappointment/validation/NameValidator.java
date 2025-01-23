package com.olegtoropoff.petcareappointment.validation;

import java.util.regex.Pattern;

/**
 * Utility class for validating and formatting names.
 * <p>
 * This class provides methods to validate names according to specific rules
 * and format them to ensure consistency in capitalization and spacing.
 */
public class NameValidator {

    /**
     * Regular expression pattern for validating names.
     * <p>
     * The pattern allows:
     * <ul>
     *     <li>Names consisting of 1 to 50 characters.</li>
     *     <li>Only letters from Latin and Cyrillic alphabets (uppercase or lowercase).</li>
     *     <li>Optional hyphen or space separating two name parts.</li>
     * </ul>
     */
    private static final Pattern NAME_PATTERN = Pattern.compile(
            "^[A-Za-zА-Яа-яЁё]{1,50}((-| )[A-Za-zА-Яа-яЁё]{1,50})?$"
    );

    /**
     * Validates the given name against the predefined rules.
     *
     * @param name the name to validate
     * @return {@code true} if the name is valid, {@code false} otherwise
     */
    public static boolean isValid(String name) {
        if (name == null || name.trim().length() < 2) {
            return false;
        }
        return NAME_PATTERN.matcher(name.trim()).matches();
    }

    /**
     * Formats the given name by ensuring proper capitalization of each part.
     * <p>
     * Example:
     * <ul>
     *     <li>Input: "john doe" → Output: "John Doe"</li>
     *     <li>Input: "иван-иванов" → Output: "Иван-Иванов"</li>
     * </ul>
     *
     * @param name the name to format
     * @return the formatted name
     */
    public static String format(String name) {
        name = name.trim();
        String[] parts = name.split("[- ]");
        StringBuilder formattedName = new StringBuilder();

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            formattedName.append(Character.toUpperCase(part.charAt(0)))
                    .append(part.substring(1).toLowerCase());
            if (i < parts.length - 1) {
                formattedName.append(name.contains("-") ? "-" : " ");
            }
        }

        return formattedName.toString();
    }
}
