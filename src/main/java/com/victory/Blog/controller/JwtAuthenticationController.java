package com.victory.Blog.controller;

import com.victory.Blog.base.user.User;
import com.victory.Blog.security.jwt.JwtRequest;
import com.victory.Blog.security.jwt.JwtResponse;
import com.victory.Blog.security.jwt.JwtTokenUtil;
import com.victory.Blog.security.service.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class JwtAuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    JwtUserDetailsService userDetailsService;

    /*Sign in, publicly accessible*/
    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {

        //Debug
        System.out.println("Bin in der mapping methode");

        Authentication auth = authenticate(authenticationRequest.getEmail(), authenticationRequest.getPassword());

        SecurityContextHolder.getContext().setAuthentication(auth);

        final String token = jwtTokenUtil.generateToken(auth);

        return ResponseEntity.ok(new JwtResponse(token));
    }

    /*Create new user, not publicly accessible*/
    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public ResponseEntity<?> saveUser(@RequestBody User user) throws Exception {
        return ResponseEntity.ok(userDetailsService.save(user));
    }

    private Authentication authenticate(String email, String password) throws Exception {

        Authentication auth;

        try {
            auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);

        } catch (BadCredentialsException e) {
            System.out.println("invalid cred");
            throw new Exception("INVALID_CREDENTIALS", e);
        }

        return auth;
    }

}
