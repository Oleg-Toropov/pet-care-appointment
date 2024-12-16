package com.olegtoropoff.petcareappointment.validation;

import java.util.regex.Pattern;

public class NameValidator {
    private static final Pattern NAME_PATTERN = Pattern.compile(
            "^[A-Za-zА-Яа-яЁё]{1,50}((-| )[A-Za-zА-Яа-яЁё]{1,50})?$"
    );

    public static boolean isValid(String name) {
        if (name == null || name.trim().length() < 2) {
            return false;
        }
        return NAME_PATTERN.matcher(name.trim()).matches();
    }

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
