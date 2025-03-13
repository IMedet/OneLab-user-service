package kz.medet.userservice.security;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class SecurityConstants {
    public static final String SIGN_UP_URLS = "/api/auth/**";
    public static final byte[] SECRET = Keys.secretKeyFor(SignatureAlgorithm.HS512).getEncoded(); // Храним ключ в виде массива байтов
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String CONTENT_TYPE = "application/json";
    public static final long EXPIRATION_TIME = 600_000; // 10мин
}

