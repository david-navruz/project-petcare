package com.project.petcare.utils;

import java.util.Calendar;
import java.util.Date;

public class SystemUtils {

    private static final int EXPIRATION_TIME = 2;

    // Pour chaque token, on a un delai d'expiration de 2 min.
    // Methode pour calculer une date d'expiration dans 2 minutes
    public static Date getExpirationTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.add(Calendar.MINUTE, EXPIRATION_TIME);
        return new Date(calendar.getTime().getTime());
    }

}
