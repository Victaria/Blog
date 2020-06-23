package com.victory.blog.base.article;

import com.victory.blog.base.tag.PostTag;
import com.victory.blog.base.tag.PostTagService;
import com.victory.blog.base.tag.Tag;
import com.victory.blog.base.tag.TagService;
import com.victory.blog.base.user.UserService;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.sql.Date;
import java.util.*;

@Transactional
@Service
public class ArticleService {

    @Inject
    private ArticleRepository articleRepository;

    @Inject
    private UserService userService;

    @Inject
    private TagService tagService;

    @Inject
    private PostTagService postTagService;

    public Article getById(int id) {
        return articleRepository.findById(id).get();
    }

    public Article getArticleByTitleAndAuthorId(String title, int author_id) {
        return articleRepository.findByTitleAndAuthorId(title, author_id);
    }

    public Article createArticle(ArticleRequest articleRequest, String email) {
        Article article = new Article();
        article.setAuthorId(userService.getByEmail(email).getId());
        article.setText(articleRequest.getText());
        article.setTitle(articleRequest.getTitle());
        article.setStatus("public");
        article.setCreatedAt(new Date(Calendar.getInstance().getTime().getTime()));

        return articleRepository.save(article);
    }

    public Page<Article> getByAuthorId(Integer author_id, Pageable pageable) {
        return articleRepository.findByAuthorId(author_id, pageable);
    }

    public List<Article> getDraftByAuthorId(Integer author_id) {
        return articleRepository.findDraftByAuthorId(author_id);
    }

    public void updateArticle(int id, Article article) {
        article.setUpdatedAt(new Date(Calendar.getInstance().getTime().getTime()));

        articleRepository.updateArticle(article.getUpdatedAt(), article.getTitle(), article.getText(), id);
    }

    public void deleteArticle(int id, String email) {
        int userId = userService.getByEmail(email).getId();
        int postAuthorId = articleRepository.getOne(id).getAuthorId();

        if (userId == postAuthorId) {
            articleRepository.deleteById(id);
        } else {
            System.out.println("no rights");
        }
    }

    public Page<Article> getPublicArticles(Pageable pageable) {
        return articleRepository.findPublicArticles(pageable);
    }

    public Page<Article> findArticleByTags(List<String> tags, Pageable pageable) {
        Set<PostTag> postTagSet = new LinkedHashSet<>();
        Set<Article> articleSet = new LinkedHashSet<>();
        Tag tag;

        for (String tagName : tags) {
            tag = tagService.getTag(tagName);
            if (tag != null) {
                postTagSet.addAll(postTagService.getAllByTagId(tag.getId()));
            }
            System.out.println(postTagSet.toString());

            for (PostTag postTag : postTagSet) {
                articleSet.add(articleRepository.findById(postTag.getPostId()).get());
            }
        }

        return new PageImpl<Article>(new ArrayList<>(articleSet), pageable, articleSet.size());
    }

    public Page<Article> filter(int skip, int limit, int author_id, String sortField, String order,
                                Pageable pageable) {

        Sort sort = Sort.by(sortField).ascending();

        if (order.equalsIgnoreCase("desc")) {
            sort = Sort.by(sortField).descending();
        }

        Page<Article> articles = articleRepository.findAll(ArticleSpecification.postAuthorId(author_id), PageRequest.of(skip, limit, sort));

        return articles;
    }

    public void updateStatusToPublic(Integer id) {
        articleRepository.updateByIdStatusToPublic(new Date(Calendar.getInstance().getTime().getTime()), id);
    }

    public void updateStatusToDraft(Integer id) {
        articleRepository.updateByIdStatusToDraft(new Date(Calendar.getInstance().getTime().getTime()), id);
    }
}
