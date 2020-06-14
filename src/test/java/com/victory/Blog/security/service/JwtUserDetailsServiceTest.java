package com.victory.Blog.security.service;

import com.victory.Blog.security.jwt.SignUpRequest;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration
@DataJpaTest
@Transactional
@AutoConfigureTestEntityManager
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class JwtUserDetailsServiceTest {

    @Autowired
    JwtUserDetailsService userDetailsService;

    @Test
    void loadUserByUsername() {
        UserDetails userDetails = userDetailsService.loadUserByUsername(save());
        System.out.println(userDetails.getUsername() + "  **  " + userDetails.getAuthorities() + " **  " + userDetails.getPassword());
    }

    @Test
    String save() {
        SignUpRequest signUpRequest = getSignUpRequest();

        userDetailsService.save(signUpRequest);

        return signUpRequest.getEmail();
    }

    SignUpRequest getSignUpRequest(){
        SignUpRequest user = new SignUpRequest();
        user.setEmail("email@email.com");
        user.setFirstname("Ida");
        user.setLastname("Levan");
        user.setPassword("112233");

        return user;
    }
}