package com.victory.Blog.security.config;

import com.victory.Blog.security.jwt.JwtTokenUtil;
import com.victory.Blog.security.service.JwtUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Nimmt HTTP-Requests entgegen und prüft, ob Sender authentifiziert ist
 * Entwerder durch einen Bearer-Token (JWT-Token) oder durch
 * Benutzername und Passwort
 */
@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    /**
     * Validation of the JWT sent with an HTTP request and authentication with SecurityContextHolder
     *
     * @param request  the received HTTP-Request
     * @param response HTTP-Response to send JWT to CLient
     * @param chain    FilterChain
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        //Extract value for the "Authorization" key in the request header
        final String requestTokenHeader = request.getHeader("Authorization");

        String email = null;
        String jwtToken = null;

        // If a JWT is delivered in the header (also known as a bearer token)
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {

            //Remove the word Bearer from the beginning of the header value
            jwtToken = requestTokenHeader.substring(7);

            //Evaluate the information encoded in the token
            try {

                //Extract the encoded username from the JWT
                email = jwtTokenUtil.getEmailFromToken(jwtToken);
            } catch (IllegalArgumentException e) {

                //JWT is not in the right format; cannot be evaluated
                System.out.println("Unable to get JWT Token");
            } catch (ExpiredJwtException e) {

                //JWT has expired; User has to authenticate again with your login data
                System.out.println("JWT Token has expired");
            } catch (SignatureException e) {

                //The token's signature is invalid
                System.out.println("Could not verify signature");
            }

        } else {
            System.out.println("JWT-String is not starting with 'Bearer'");
        }

        /* If the username could be extracted successfully and
         *  no authentication information is already available
         *  (are never available because no session is saved) */
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            /* Check via the service in the database whether a user with
             *  the transmitted user name exists, if yes -> Service returns user
             *  implements user details with information on user name, password, authorizations*/
            UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(email);

            //If user name from DB and token match and the JWT has not expired
            if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {

                /* Generate UPAT, which is passed to the SecurityContextHolder
                 *  so that the request can pass the filter (authenticated) */
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        jwtTokenUtil.getAuthentication(jwtToken, SecurityContextHolder.getContext().getAuthentication(),
                                userDetails);

                // upat to HTTP-Request transfer
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                //Debug,
                System.out.println("User " + email + " wurde authentifiziert");

                // Inform the SecurityContextHolder that the user is authenticated and may pass the filter
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }

        }

        chain.doFilter(request, response);
    }
}
