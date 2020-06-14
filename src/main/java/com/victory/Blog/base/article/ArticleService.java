package com.victory.Blog.base.article;

import com.victory.Blog.base.tag.PostTag;
import com.victory.Blog.base.tag.PostTagService;
import com.victory.Blog.base.tag.Tag;
import com.victory.Blog.base.tag.TagService;
import com.victory.Blog.base.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.sql.Date;
import java.util.*;

@Transactional
@Service
public class ArticleService{

    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    UserService userService;

    @Autowired
    TagService tagService;

    @Autowired
    PostTagService postTagService;

    public Optional<Article> getById(int id){
        return articleRepository.findById(id);
    }

    public Article getArticleByTitleAndAuthorId(String title, int author_id){
       return articleRepository.findByTitleAndAuthorId(title, author_id);
    }

    public Article createArticle(ArticleRequest articleRequest, HttpSession session) {
        Article article = new Article();
        article.setAuthorId(userService.getByEmail((String) session.getAttribute("email")).getId());
        article.setText(articleRequest.getText());
        article.setTitle(articleRequest.getTitle());
        article.setStatus("public");
        article.setCreatedAt(new Date(Calendar.getInstance().getTime().getTime()));

       return articleRepository.save(article);
    }

    public Page<Article> getByAuthorId(Integer author_id, Pageable pageable){
        return articleRepository.findByAuthorId(author_id, pageable);
    }

    public List<Article> getDraftByAuthorId(Integer author_id){
        return articleRepository.findDraftByAuthorId(author_id);
    }

    public HttpStatus updateArticle(int id, Article article) {
        article.setUpdatedAt(new Date(Calendar.getInstance().getTime().getTime()));

        articleRepository.updateArticle(article.getUpdatedAt(), article.getTitle(), article.getText(), id);

        return HttpStatus.OK;
    }

    public HttpStatus deleteArticle(int id, HttpSession session) {
        int userId = userService.getByEmail((String) session.getAttribute("email")).getId();
        int postAuthorId = articleRepository.getOne(id).getAuthorId();

        if (userId == postAuthorId) {
            articleRepository.deleteById(id);
        } else {
            System.out.println("no rights");
        }

        return HttpStatus.OK;
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
        Page<Article> articlePage = new PageImpl<Article>(new ArrayList<>(articleSet), pageable, articleSet.size());

        return articlePage;
    }

    public Page<Article> filter(int skip, int limit, int author_id, String sortField, String order,
                                 Pageable pageable){

        Sort sort = Sort.by(sortField).ascending();

        if (order.equalsIgnoreCase("desc")) {
            sort = Sort.by(sortField).descending();
        }

        List<Article> articles = articleRepository.findAll(ArticleSpecification.postAuthorId(author_id), sort);

        return new PageImpl<Article>(articles, pageable, articles.size());
    }

}
