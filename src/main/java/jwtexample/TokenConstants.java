package jwtexample;

final class TokenConstants {
    static final String TOKEN_ISSUER = "justme";
    static final String TOKEN_HEADER = "Authorization";
    static final String TOKEN_PREFIX = "Bearer ";
    static final long EXPIRATION_TIME = 60_000; // one minute
}
