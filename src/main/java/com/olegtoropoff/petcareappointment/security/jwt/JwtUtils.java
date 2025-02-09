package com.olegtoropoff.petcareappointment.security.jwt;

import com.olegtoropoff.petcareappointment.security.user.UPCUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;

/**
 * Utility class for generating, parsing, and validating JSON Web Tokens (JWT).
 */
@Component
public class JwtUtils {

    /**
     * Secret key for signing JWT, injected from application properties.
     */
    @Value("${auth.token.jwtSecret}")
    private String jwtSecret;

    /**
     * JWT expiration time in milliseconds, injected from application properties.
     */
    @Value("${auth.token.expirationInMils}")
    private int jwtExpirationMs;

    /**
     * Generates a JWT for a user based on their authentication details.
     *
     * @param authentication the user's authentication object
     * @return a signed JWT as a string
     */
    public String generateTokenForUser(Authentication authentication) {
        UPCUserDetails userPrincipal = (UPCUserDetails) authentication.getPrincipal();

        List<String> roles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .claim("id", userPrincipal.getId())
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS256).compact();
    }

    /**
     * Decodes the secret key and returns it as a {@link Key} for signing JWTs.
     *
     * @return a {@link Key} for signing JWTs
     */
    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    /**
     * Extracts the username (email) from a JWT.
     *
     * @param token the JWT string
     * @return the username stored in the token
     */
    public String getUserNameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * Validates a JWT by parsing it and checking its integrity and expiration.
     *
     * @param token the JWT string to validate
     * @return true if the token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parse(token);
            return true;
        } catch (MalformedJwtException | IllegalArgumentException | UnsupportedJwtException | ExpiredJwtException e) {
            return false;
        }
    }
}
