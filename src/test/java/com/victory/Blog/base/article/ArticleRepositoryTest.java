package com.victory.blog.base.article;

import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ArticleRepositoryTest {

   /* @Inject
    private ArticleRepository articleRepository;

    @Test
    void updateArticle() {
        String title = "Preview part 1";
        articleRepository.updateArticle(new Date(Calendar.getInstance().getTime().getTime()), title, "Hi friends, it is my first post.", 1);
        Article article = articleRepository.findByTitleAndAuthorId(title, 1);

        Assert.assertEquals(article.getUpdatedAt().toString().trim(), new Date(Calendar.getInstance().getTime().getTime()).toString().trim());
        Assert.assertEquals(article.getTitle().trim(), title.trim());
    }

    @Test
    void findDraftByAuthorId() {
        Article article = new Article();
        article.setAuthorId(99);
        article.setTitle("Test article");
        article.setText("This article was created only for tests.");
        article.setCreatedAt(new Date(Calendar.getInstance().getTime().getTime()));
        article.setStatus("draft");
        articleRepository.save(article);

        List<Article> actualArticleList = articleRepository.findDraftByAuthorId(99);
        for (Article testArticle : actualArticleList){
            Assert.assertEquals(testArticle.getAuthorId(), article.getAuthorId());
            Assert.assertEquals(testArticle.getStatus(), article.getStatus());
        }
    }
*/
}