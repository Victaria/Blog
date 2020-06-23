package com.victory.blog.controller;

import com.victory.blog.security.jwt.SignUpRequest;
import com.victory.blog.security.service.JwtUserDetailsService;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.inject.Inject;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ControllerTest {

    private MockMvc mockMvc;

    @Inject
    private WebApplicationContext wac;

    @Before
    public void setup(){
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        JwtUserDetailsService user = new JwtUserDetailsService();
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setEmail("vvvtest@gmail.com");
        signUpRequest.setFirstname("Elena");
        signUpRequest.setLastname("Volkova");
        signUpRequest.setPassword("11235");
        user.save(signUpRequest);
    }

    @Test
    public void getAllArticles() throws Exception {
        this.mockMvc.perform(get("/articles"))
                .andReturn().getModelAndView().getModel().get("articles");
                //andDo(print()).andExpect(view().name("afterAuth/main"));
    }

    @Test
    public void getCommentsByPostId() throws Exception {
        this.mockMvc.perform(get("/blog/articles/{post_id}/comments", 1)).andExpect(status().isOk()).andDo(print());
    }

    @Test
    public void showUserProfile() {
    }

    @Test
    public void getPostsCount() {
    }
}