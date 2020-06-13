package com.victory.Blog.base.article;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;


@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ArticleRepositoryTest {

    @Autowired
    ArticleRepository articleRepository;

    @Test
    void updateArticle() {
        String title = "Preview part 1";
        articleRepository.updateArticle(new Date(Calendar.getInstance().getTime().getTime()), title, "Hi friends, it is my first post.", 1);
        Article article = articleRepository.findByTitleAndAuthorId(title, 1);

        Assert.assertEquals(article.getUpdated_at().toString().trim(), new Date(Calendar.getInstance().getTime().getTime()).toString().trim());
        Assert.assertEquals(article.getTitle().trim(), title.trim());
    }

    @Test
    void findDraftByAuthorId() {
        Article article = new Article();
        article.setAuthor_id(99);
        article.setTitle("Test article");
        article.setText("This article was created only for tests.");
        article.setCreated_at(new Date(Calendar.getInstance().getTime().getTime()));
        article.setStatus("draft");
        articleRepository.save(article);

        List<Article> actualArticleList = articleRepository.findDraftByAuthorId(99);
        for (Article testArticle : actualArticleList){
            Assert.assertEquals(testArticle.getAuthor_id(), article.getAuthor_id());
            Assert.assertEquals(testArticle.getStatus(), article.getStatus());
        }
    }
}