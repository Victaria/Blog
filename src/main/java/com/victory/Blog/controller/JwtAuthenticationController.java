package com.victory.Blog.controller;

import com.victory.Blog.base.article.ArticleRepository;
import com.victory.Blog.base.user.User;
import com.victory.Blog.base.user.UserRepository;
import com.victory.Blog.security.auth.EmailService;
import com.victory.Blog.security.jwt.JwtRequest;
import com.victory.Blog.security.jwt.JwtTokenUtil;
import com.victory.Blog.security.jwt.SignUpRequest;
import com.victory.Blog.security.service.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

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
    private ArticleRepository articleRepository;

    @Autowired
    EmailService mailer = new EmailService();

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView getLoginForm() {
        ModelAndView mav = new ModelAndView("authorisation/login", "authenticationRequest", new JwtRequest());
        mav.addObject("error", "");
        return mav;
    }

    /*Sign in, publicly accessible*/
    @RequestMapping(value = "/authenticate", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ModelAndView createAuthenticationToken(@ModelAttribute JwtRequest authenticationRequest,
                                                  HttpSession session) throws Exception {

        //Debug
        System.out.println("Bin in der mapping methode");

        if (authenticate(authenticationRequest.getEmail(), authenticationRequest.getPassword()) != null) {
            Authentication auth = authenticate(authenticationRequest.getEmail(), authenticationRequest.getPassword());

            SecurityContextHolder.getContext().setAuthentication(auth);

            final String token = jwtTokenUtil.generateToken(auth);

            session.setAttribute("email", authenticationRequest.getEmail());

            return new ModelAndView("redirect:/blog/my");
        } else {
            ModelAndView mav = new ModelAndView("authorisation/login", "authenticationRequest", new JwtRequest());
            mav.addObject("error", "E-mail or Password are incorrect.");
            return mav;
        }
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView logOut(HttpSession session) {
        session.removeAttribute("email");
        return new ModelAndView("redirect:/login", "authenticationRequest", new JwtRequest());
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

        User user = new User();
        user.setFirstname(signUpRequest.getFirstname());
        user.setLastname(signUpRequest.getLastname());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(signUpRequest.getPassword());

        userDetailsService.save(user);

        final String token = signUpRequest.getHash();

        mailer.sendMail(signUpRequest.getEmail(), "Dear " + user.getFirstname()
                + ", please, confirm your email. " + '\n'
                + "Follow this link: " + "http://localhost:8080/auth/confirm/" + token);


        return new ModelAndView("info/sentToEmail", "user", user);
    }

    private Authentication authenticate(String email, String password) throws Exception {

        Authentication auth;

        try {
            auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

        } catch (DisabledException e) {
          //  throw new Exception("USER_DISABLED", e);
            return null;
        } catch (BadCredentialsException e) {
            System.out.println("invalid cred");
            return null;
           // throw new Exception("INVALID_CREDENTIALS", e);
        }

        return auth;
    }

}
