package com.victory.Blog.base.article;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Integer>, JpaSpecificationExecutor<Article> {

    @Query("SELECT t FROM Article t WHERE t.authorId = ?1 ")
    Page<Article> findByAuthorId(Integer author_id, Pageable pageable);

    @Query("SELECT t FROM Article t WHERE t.status = 'public' ")
    Page<Article> findPublicArticles(Pageable pageable);

    @Query("SELECT t FROM Article t WHERE t.authorId = ?1 AND t.status = 'draft' ")
    List<Article> findDraftByAuthorId(Integer author_id);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("update Article a set a.updatedAt = ?1 , a.status = 'public' where a.id = ?2")
    void updateByIdStatusToPublic(Date date, Integer id);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("update Article a set a.updatedAt = ?1 , a.status = 'draft' where a.id = ?2")
    void updateByIdStatusToDraft(Date date, Integer id);

    @Query("SELECT t FROM Article t WHERE t.authorId = ?2 AND t.title = ?1 ")
    Article findByTitleAndAuthorId(String title, Integer author_id);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("update Article a set a.updatedAt = ?1 , a.title = ?2 , a.text = ?3 where a.id = ?4")
        //set confirmed = true
    void updateArticle(Date date, String title, String text, int id);

}
