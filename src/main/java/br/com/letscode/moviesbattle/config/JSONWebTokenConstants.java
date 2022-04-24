package br.com.letscode.moviesbattle.config;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class JSONWebTokenConstants {
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_KEY = AUTHORIZATION;
    public static final String LOGIN_URL = "/api/login";
    public static final String INPUT_USERNAME_KEY = "username";
    public static final String INPUT_PASSWORD_KEY = "password";
}
