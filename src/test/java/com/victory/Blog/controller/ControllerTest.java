package com.victory.Blog.controller;

import com.victory.Blog.base.article.Article;
import com.victory.Blog.base.article.ArticleRepository;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.sql.Date;
import java.util.Calendar;

@RunWith(MockitoJUnitRunner.class)
@WebAppConfiguration
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private Controller controller;

    @Autowired(required=true)
    ArticleRepository articleRepository;

    @Before
    public void setup() {
        controller = new Controller();
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    private void insertDraftArticles(){
        articleRepository.save(new Article("title 1", "text text 1", "draft", 105, new Date(Calendar.getInstance().getTime().getTime())));
        articleRepository.save(new Article("title 2", "text text 2", "draft", 105, new Date(Calendar.getInstance().getTime().getTime())));
    }

  /*  @Test
    void findByTags() throws Exception {
        ResultActions result = mockMvc.perform(get("/search/articles").requestAttr("tags", "first,post").accept("text/html"))
        .andExpect(MockMvcResultMatchers.view().name("articles/main"));
    }*/
}