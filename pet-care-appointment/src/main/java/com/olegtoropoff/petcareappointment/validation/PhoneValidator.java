package com.olegtoropoff.petcareappointment.validation;
import java.util.regex.Pattern;

public class PhoneValidator {
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

    private static final Pattern PHONE_PATTERN = Pattern.compile(PHONE_REGEX);

    public static boolean isValid(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phoneNumber).matches();
    }
}
