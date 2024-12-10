package com.olegtoropoff.petcareappointment.validation;

import java.util.regex.Pattern;

public class NameValidator {
    private static final Pattern NAME_PATTERN = Pattern.compile(
            "^[A-ZА-ЯЁ][a-zа-яё]{1,49}((-| )[A-ZА-ЯЁ][a-zа-яё]{1,49})?$"
    );

    public static boolean isValid(String name) {
        if (name == null || name.trim().length() < 2) {
            return false; // Null или слишком короткая строка
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
