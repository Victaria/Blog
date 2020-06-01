package com.victory.Blog.security.jwt;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtTokenUtil implements Serializable {
    //Token is 24 hour valid
    public static final long JWT_TOKEN_VALIDITY = 24 * 60 * 60 * 1000;

    //Secret to sign JWT
    // @Value("${jwt.secret}")
    private String SIGNING_KEY;

    private static final String AUTHORITIES_KEY = "scopes";

    /**
     * Username from JWT extract
     *
     * @param token TokenString
     * @return String username
     */
    public String getEmailFromToken(String token) {

        return getClaimFromToken(token, Claims::getSubject);

    }

    /**
     * Extract the expiration date from the JWT
     *
     * @param token TokenString
     * @return Date - expiration date
     */
    public Date getExpirationDateFromToken(String token) {

        return getClaimFromToken(token, Claims::getExpiration);

    }

    /**
     * Auxiliary method to extract the claims
     */
    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {

        final Claims claims = getAllClaimsFromToken(token);

        return claimsResolver.apply(claims);

    }

    /**
     * Use the secret to extract the information from the session token
     *
     * @param token String
     * @return
     */
    private Claims getAllClaimsFromToken(String token) {

        return Jwts.parser().setSigningKey(SIGNING_KEY).parseClaimsJws(token).getBody();

    }

    private Boolean isTokenExpired(String token) {

        final Date expiration = getExpirationDateFromToken(token);

        return expiration.before(new Date());

    }

    /**
     * generate new JWT for User
     *
     * @param authentication Authentication
     * @return String JWT
     */
    public String generateToken(Authentication authentication) {

        // Permissions separated by commas
        final String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        // JWT generate
        return Jwts.builder()
                .setSubject(authentication.getName()) // Username
                .claim(AUTHORITIES_KEY, authorities) // Role
                .signWith(SignatureAlgorithm.HS256, SIGNING_KEY) // Signature
                .setIssuedAt(new Date(System.currentTimeMillis())) // Date of issue
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY)) // Expiry Date
                .compact();

    }

    /**
     * Check whether the information provided by the token matches the information saved
     *
     * @param token       String transmitted jwt
     * @param userDetails UserDetails from Database, are fetched from the DB by UserDetails Service
     * @return true, if Username (from Token and DB) match and the token has not expired
     */
    public Boolean validateToken(String token, UserDetails userDetails) {

        //Username from Token parse
        final String username = getEmailFromToken(token);

        if (username.equals(userDetails.getUsername()) && !isTokenExpired(token)) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * UPAT generate, which contains all user details and permissions
     *
     * @param token        String JWT
     * @param existingAuth Authentication from SecurityContextHolder().getContext().getAuthentication()
     * @param userDetails  UserDetails from Database
     * @return UPAT with all user details and permissions
     */
    public UsernamePasswordAuthenticationToken getAuthentication(final String token, final Authentication existingAuth,
                                                                 final UserDetails userDetails) {

        //Set the secret key that is used to check the signature
        final JwtParser jwtParser = Jwts.parser().setSigningKey(SIGNING_KEY);

        //Extract claims from the JWT and check whether they are signed
        final Jws<Claims> claimsJws = jwtParser.parseClaimsJws(token);

        final Claims claims = claimsJws.getBody();

        //claims to Collection (Interface) write
        final Collection authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        //Claims and UserDetails in UPAT put
        return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
    }


}
