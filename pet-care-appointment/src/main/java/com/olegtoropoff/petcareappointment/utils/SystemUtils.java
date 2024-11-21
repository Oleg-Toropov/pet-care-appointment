package com.olegtoropoff.petcareappointment.utils;

import java.util.Calendar;
import java.util.Date;

public class SystemUtils {
    private static final int EXPIRATION_TIME = 30;

    public static Date getExpirationTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.add(Calendar.MINUTE, EXPIRATION_TIME);
        return new Date(calendar.getTime().getTime());
    }
}
