package com.victory.blog.controller;

import com.victory.blog.base.user.User;
import com.victory.blog.base.user.UserEmail;
import com.victory.blog.base.user.UserService;
import com.victory.blog.security.auth.EmailService;
import com.victory.blog.security.jwt.JwtRequest;
import com.victory.blog.security.jwt.JwtTokenUtil;
import com.victory.blog.security.jwt.SignUpRequest;
import com.victory.blog.security.service.JwtUserDetailsService;
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
import redis.clients.jedis.Jedis;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

@RestController
public class JwtAuthenticationController {
    @Inject
    private AuthenticationManager authenticationManager;

    @Inject
    private JwtTokenUtil jwtTokenUtil;

    @Inject
    private JwtUserDetailsService userDetailsService;

    @Inject
    private UserService userService;

   // @Inject
   // private RedisServer redis;
    private Jedis jedis = new Jedis("127.0.0.1");

    @Inject
    private EmailService mailer = new EmailService();

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView getLoginForm() {
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

        if (authenticate(authenticationRequest.getEmail(), authenticationRequest.getPassword()) != null) {
            Authentication auth = authenticate(authenticationRequest.getEmail(), authenticationRequest.getPassword());

            if (userService.getByEmail(authenticationRequest.getEmail()).getConfirmed()) {
                SecurityContextHolder.getContext().setAuthentication(auth);

                final String token = jwtTokenUtil.generateToken(auth);

                session.setAttribute("email", authenticationRequest.getEmail());

                return new ModelAndView("redirect:/blog/my");
            } else {

                ModelAndView mav = new ModelAndView("authorisation/login", "authenticationRequest", new JwtRequest());
                mav.addObject("error", "You did not confirm e-mail.");
                return mav;
            }
        } else {

            ModelAndView mav = new ModelAndView("authorisation/login", "authenticationRequest", new JwtRequest());
            mav.addObject("error", "E-mail or Password are incorrect.");
            return mav;
        }
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public ModelAndView logOut(HttpSession session) {
        session.removeAttribute("email");
        return new ModelAndView("redirect:/login", "authenticationRequest", new JwtRequest());
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public ModelAndView getRegisterForm() {
        ModelAndView mav = new ModelAndView("authorisation/register", "user", new User());
        return mav;
    }

    @Transactional
    @RequestMapping(value = "/registerProcess", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ModelAndView saveUser(@ModelAttribute SignUpRequest signUpRequest) throws Exception {

        if (userService.getByEmail(signUpRequest.getEmail()) == null) {

            userDetailsService.save(signUpRequest);

            User user = userService.getByEmail(signUpRequest.getEmail());

            final String token = SignUpRequest.generateHash(10);

            jedis.set(token, userService.getByEmail(user.getEmail()).getId().toString());
            jedis.expire(token, 86400);

            mailer.mailForRegistration(user, token);

            return new ModelAndView("info/sentToEmail", "text",
                    "Link for registration confirmation was sent to your email");
        } else {
            ModelAndView mav = new ModelAndView("authorisation/register", "user", new User());
            mav.addObject("error", "This email is already in use.");
            return mav;
        }
    }

    @Transactional
    @RequestMapping(value = "/auth/confirm/{hash}", method = RequestMethod.GET)
    public ModelAndView validateAccount(@PathVariable String hash) {
        if (jedis.exists(hash)) {
            System.out.println(jedis.get(hash));

            User user = userService.getById(Integer.parseInt(jedis.get(hash)));
            user.setConfirmed(true);
            jedis.del(hash);
            return new ModelAndView("info/information", "info", "You've successfully confirmed your email. You can login now.");//successfully confirmed form
        } else {
            return new ModelAndView("info/information", "info", "Something went wrong. Url is not valid.");
        }
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

    /**
     * Password recovery
     */

    @Transactional
    @GetMapping(value = "/auth/forgot_password")
    public ModelAndView getForm() {
        return new ModelAndView("recovery/passwordForgotForm", "user_email", new UserEmail());
    }

    @Transactional
    @RequestMapping(value = "/auth/forgot_password", method = RequestMethod.POST)
    public ModelAndView sendToMailPassword(@ModelAttribute UserEmail user_email) {
        String email = user_email.getEmail();
        if (!email.isEmpty()) {
            User user = userService.getByEmail(email);
            if (user != null) {
                final String token = SignUpRequest.generateHash(12);

                jedis.set(token, user.getId().toString());
                jedis.expire(token, 172800);

                mailer.mailForReset(user, token);

                return new ModelAndView("info/sentToEmail", "text",
                        "Link for password changing was sent to your email.");
            } else {
                return new ModelAndView("info/information", "info",
                        "User with such email did not exist.");
            }
        } else {
            ModelAndView modelAndView = new ModelAndView("recovery/passwordForgotForm", "user_email", new UserEmail());
            modelAndView.addObject("error", "Please, enter e-mail.");
            return modelAndView;
        }
    }

    @Transactional
    @GetMapping(value = "/auth/reset/{hash}")
    public ModelAndView sendToMailPassword(@PathVariable String hash, HttpSession httpSession) {
        if (jedis.exists(hash)) {
            User user = userService.getById(Integer.parseInt(jedis.get(hash)));

            httpSession.setAttribute("email", user.getEmail());
            return new ModelAndView("recovery/enterPassword", "jwtRequest", new JwtRequest());
        } else {
            return new ModelAndView("info/information", "info", "Something went wrong. Url is not valid.");
        }
    }

    @Transactional
    @PostMapping(value = "/auth/reset")
    public ModelAndView saveNewPassword(@ModelAttribute JwtRequest jwtRequest, HttpSession httpSession) {
        if (!jwtRequest.getPassword().isEmpty()) {
            User user = userService.getByEmail(httpSession.getAttribute("email").toString());
            user.setPassword(jwtRequest.getPassword());

            userDetailsService.update(user);

            return new ModelAndView("info/information", "info", "Password was reset.");
        } else {
            ModelAndView modelAndView = new ModelAndView("recovery/enterPassword", "jwtRequest", jwtRequest);
            modelAndView.addObject("error", "Please, enter correct password.");
            return modelAndView;
        }
    }
}
