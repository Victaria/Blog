package com.victory.Blog.base.article;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Integer> {

    @Query("SELECT t FROM Article t WHERE t.author_id = ?1 ")
    List<Article> findByAuthorId(Integer author_id);

    @Query("SELECT t FROM Article t WHERE t.status = 'public' ")
    List<Article> findPublicArticles();

    @Query("SELECT t FROM Article t WHERE t.author_id = ?1 AND t.status = 'draft' ")
    List<Article> findDraftByAuthorId(Integer author_id);

    @Query("SELECT t FROM Article t WHERE t.author_id = ?2 AND t.title = ?1 ")
    Article findByTitleAndAuthorId(String title, Integer author_id);

}
