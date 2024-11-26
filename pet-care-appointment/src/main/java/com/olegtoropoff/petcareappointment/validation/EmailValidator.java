package com.olegtoropoff.petcareappointment.validation;

import java.util.regex.Pattern;

public class EmailValidator {
    private static final String EMAIL_REGEX =
            "^[a-zA-Z0-9._%+-]+" +           // The local part: allows letters, numbers, and special characters like ., _, %, +, and -
                    "@" +                            // The @ symbol separating the local part and the domain
                    "(" +                            // Start of the domain options
                    "icloud\\.com|" +                // Domain: icloud.com
                    "me\\.com|" +                    // Domain: me.com
                    "mac\\.com|" +                   // Domain: mac.com
                    "yahoo\\.com|" +                 // Domain: yahoo.com
                    "outlook\\.com|" +               // Domain: outlook.com
                    "hotmail\\.com|" +               // Domain: hotmail.com
                    "live\\.com|" +                  // Domain: live.com
                    "msn\\.com|" +                   // Domain: msn.com
                    "gmail\\.com|" +                 // Domain: gmail.com
                    "pochta\\.ru|" +                 // Domain: pochta.ru
                    "cloud\\.ru|" +                  // Domain: cloud.ru
                    "rambler\\.ru|" +                // Domain: rambler.ru
                    "lenta\\.ru|" +                  // Domain: lenta.ru
                    "autorambler\\.ru|" +            // Domain: autorambler.ru
                    "myrambler\\.ru|" +              // Domain: myrambler.ru
                    "ro\\.ru|" +                     // Domain: ro.ru
                    "yandex\\.ru|" +                 // Domain: yandex.ru
                    "ya\\.ru|" +                     // Domain: ya.ru
                    "yandex\\.com|" +                // Domain: yandex.com
                    "mail\\.ru|" +                   // Domain: mail.ru
                    "bk\\.ru|" +                     // Domain: bk.ru
                    "list\\.ru|" +                   // Domain: list.ru
                    "inbox\\.ru" +                   // Domain: inbox.ru
                    ")$";                            // End of the domain options and end of the string

    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public static boolean isValid(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }
}