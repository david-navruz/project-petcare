package com.project.petcare.email;

import lombok.Data;

@Data
public class EmailProperties {
    public static final String DEFAULT_HOST = "smtp.gmail.com";
    public static final int DEFAULT_PORT = 587;
    public static final String DEFAULT_SENDER = "petcare-test@gmail.com";
    public static final String DEFAULT_USERNAME = "petcare-test@gmail.com";
    public static final String DEFAULT_PASSWORD = "rxuihkhrsdxxugfv";
    public static final boolean DEFAULT_AUTH = true;
    public static final boolean DEFAULT_STARTTLS = true;
}
