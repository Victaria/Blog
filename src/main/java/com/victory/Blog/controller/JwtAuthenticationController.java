package com.victory.Blog.controller;

import com.victory.Blog.base.user.User;
import com.victory.Blog.base.user.UserRepository;
import com.victory.Blog.security.auth.EmailService;
import com.victory.Blog.security.jwt.JwtRequest;
import com.victory.Blog.security.jwt.JwtResponse;
import com.victory.Blog.security.jwt.JwtTokenUtil;
import com.victory.Blog.security.jwt.SignUpRequest;
import com.victory.Blog.security.service.JwtUserDetailsService;
import org.apache.catalina.core.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
@CrossOrigin
public class JwtAuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    JwtUserDetailsService userDetailsService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    EmailService mailer = new EmailService();

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView getLoginForm() {
        return new ModelAndView("authorisation/login", "authenticationRequest", new JwtRequest());
    }

    /*Sign in, publicly accessible*/
    @RequestMapping(value = "/authenticate", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> createAuthenticationToken(@ModelAttribute JwtRequest authenticationRequest) throws Exception {

        //Debug
        System.out.println("Bin in der mapping methode");

        Authentication auth = authenticate(authenticationRequest.getEmail(), authenticationRequest.getPassword());

        SecurityContextHolder.getContext().setAuthentication(auth);

        final String token = jwtTokenUtil.generateToken(auth);

        return ResponseEntity.ok(new JwtResponse(token));
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView getRegisterForm() {
        return new ModelAndView("authorisation/register", "user", new User());
    }

    @Transactional
    @RequestMapping(value = "/registerProcess", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public @ResponseBody
    ModelAndView saveUser(@ModelAttribute SignUpRequest signUpRequest) throws Exception {
/*        if(userRepository.findByEmail(signUpRequest.getEmail()) != null) {
            System.out.println("already taken");
          //  return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }*/
        User user = new User();
        user.setFirstname(signUpRequest.getFirstname());
        user.setLastname(signUpRequest.getLastname());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(signUpRequest.getPassword());

        userDetailsService.save(user);

        mailer.sendMail(signUpRequest.getEmail(), "Dear " + user.getFirstname()
                + ", please, confirm your email. " + '\n'
                + "Follow this link: " + "http://localhost:8080/login" /*+ jwtTokenUtil.generateToken()*/);
        return new ModelAndView("info/sentToEmail", "user", user);
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
