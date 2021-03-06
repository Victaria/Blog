package com.victory.blog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.victory.blog.base.user.UserService;
import com.victory.blog.security.service.JwtUserDetailsService;
import org.junit.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import javax.inject.Inject;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class JwtAuthenticationControllerTest {

    private MockMvc mockMvc;

    @Inject
    private ObjectMapper objectMapper;

    @Inject
    private UserService userService;

    @Inject
    private JwtUserDetailsService userDetailsService;


    @Test
    void getRegisterForm() throws Exception {
        this.mockMvc.perform(get("/register"))
                .andExpect(view().name("authorisation/register"));
    }

    @Test
    void getForgotForm() throws Exception {
        this.mockMvc.perform(get("/auth/forgot_password"))
                .andExpect(status().isOk());
    }

   /* @Test
    void authenticate() throws Exception {
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setEmail("vvv@gmail.com");
        signUpRequest.setPassword("12345");
        signUpRequest.setFirstname("Anna");
        signUpRequest.setLastname("Kornova");

        User user =  userDetailsService.save(signUpRequest);
        JwtRequest jwtRequest = new JwtRequest();
        jwtRequest.setEmail(user.getEmail());
        jwtRequest.setPassword(user.getPassword());

        mockMvc.perform(post("/authenticate")
                .sessionAttr("jwtRequest", jwtRequest)
                .flashAttr("jwtRequest", jwtRequest))
                .andExpect(status().isOk());
    }

    @Test
    void registrationWorksThroughAllLayers() throws Exception {
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setEmail("vvv@gmail.com");
        signUpRequest.setPassword("12345");
        signUpRequest.setFirstname("Anna");
        signUpRequest.setLastname("Kornova");

        mockMvc.perform(post("http://localhost:8080/registerProcess")
                .requestAttr("signUpRequest", signUpRequest)
                .accept(MediaType.ALL))
                .andExpect(status().isOk());

        User user = userService.getByEmail(signUpRequest.getEmail());
        Assert.assertEquals(user.getEmail(), signUpRequest.getEmail());
    }*/
}