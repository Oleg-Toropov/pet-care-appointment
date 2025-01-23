package com.olegtoropoff.petcareappointment.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * Utility class providing system-level functionalities.
 * <p>
 * This class includes methods for calculating expiration times and other
 * time-related operations. Primarily used in token management and session handling.
 */
public class SystemUtils {

    /**
     * The default expiration time in minutes.
     */
    private static final int EXPIRATION_TIME = 120;

    /**
     * Calculates the expiration time from the current moment.
     * <p>
     * Adds a fixed expiration time (in minutes) to the current date and time
     * and returns the resulting {@link Date}.
     *
     * @return A {@link Date} object representing the expiration time.
     */
    public static Date getExpirationTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.add(Calendar.MINUTE, EXPIRATION_TIME);
        return new Date(calendar.getTime().getTime());
    }
}
