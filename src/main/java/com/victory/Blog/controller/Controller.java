package com.victory.Blog.controller;

import com.victory.Blog.base.article.Article;
import com.victory.Blog.base.article.ArticleRepository;
import com.victory.Blog.base.article.ArticleRequest;
import com.victory.Blog.base.comment.Comment;
import com.victory.Blog.base.comment.CommentRepository;
import com.victory.Blog.base.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.sql.Date;
import java.util.Calendar;

@org.springframework.stereotype.Controller
@RequestMapping(path = "/blog")
public class Controller {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private CommentRepository commentRepository;

    /**
     * Show all public articles
     */
    @GetMapping(path = "/articles")
    public @ResponseBody
    ModelAndView getAllArticles(HttpSession session) {

        ModelAndView mav = new ModelAndView("articles/main");

        if (session.getAttribute("email") != null) {

            mav = new ModelAndView("afterAuth/main");

            mav.addObject("user", userRepository.findByEmail((String) session.getAttribute("email")));
        }

        mav.addObject("articles", articleRepository.findPublicArticles());

        return mav;
    }

    /**
     * Get comments for post
     */
    @GetMapping(path = "/articles/{post_id}/comments")
    public @ResponseBody
    ModelAndView getCommentsByPostId(@PathVariable int post_id, HttpSession session) {

        ModelAndView mav = new ModelAndView("articles/comments");
        mav.addObject("comments", commentRepository.findByPostId(post_id));

        if (session.getAttribute("email") != null) {
            mav.addObject("user", userRepository.findByEmail((String) session.getAttribute("email")));
            mav.addObject("post_id", post_id);
            mav.addObject("new_comment", new Comment());
        } else {
            mav.addObject("user", null);
        }
        return mav;
    }

    /**
     * Crate new article
     */
    //@Secured("ROLE_USER")
    @Transactional
    @RequestMapping(value = "/articles", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    ModelAndView newArticle(@ModelAttribute ArticleRequest articleRequest, HttpSession session) {

        Article article = new Article();
        article.setAuthor_id(userRepository.findByEmail((String) session.getAttribute("email")).getId());
        article.setText(articleRequest.getText());
        article.setTitle(articleRequest.getTitle());
        article.setStatus("public");
        article.setCreated_at(new Date(Calendar.getInstance().getTime().getTime()));

        System.out.println("title: " + articleRequest.getTitle() + "text: " + articleRequest.getText());

        articleRepository.save(article);

        return new ModelAndView("redirect:/blog/my");
    }

    /**
     * Show user profile
     */
    @GetMapping("/my")
    public @ResponseBody
    ModelAndView showUserProfile(@NonNull HttpSession session) {

        System.out.println(session.getAttribute("email"));

        if (session.getAttribute("email") != null) {
            ModelAndView mav = new ModelAndView("afterAuth/profile", "articleRequest", new ArticleRequest());

            mav.addObject("articles", articleRepository.findByAuthorId(userRepository.findByEmail((String) session.getAttribute("email")).getId()));
            mav.addObject("user", userRepository.findByEmail((String) session.getAttribute("email")));
            mav.addObject("templates", articleRepository.findDraftByAuthorId(userRepository.findByEmail((String) session.getAttribute("email")).getId()));

            return mav;
        } else {
            return new ModelAndView("redirect:/blog/articles");
        }
    }

    /**
     * Create comment
     */
    @RequestMapping(value = "/articles/{id}/comments", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    ModelAndView newComment(@PathVariable int id, @ModelAttribute Comment comment, HttpSession session) {
        comment.setCreated_at(new Date(Calendar.getInstance().getTime().getTime()));
        comment.setAuthor_id(userRepository.findByEmail((String) session.getAttribute("email")).getId());
        comment.setPost_id(id);
        System.out.println(id);

        commentRepository.save(comment);

        return new ModelAndView("redirect:/blog/articles/" + id + "/comments");
    }

    /**
     * Delete article
     */
    @Secured("ROLE_USER")
    @Transactional
    @RequestMapping(value = "/articles/{post_id}", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView deleteArticle(@PathVariable int post_id, HttpSession session) {
        int userId = userRepository.findByEmail((String) session.getAttribute("email")).getId();
        int postAuthorId = articleRepository.getOne(post_id).getAuthor_id();

        System.out.println(userId + "  " + postAuthorId);

        if (userId == postAuthorId) {
            articleRepository.deleteById(post_id);
        } else {
            System.out.println("no rights");
        }

        return new ModelAndView("redirect:/blog/my");
    }


    /**
     * Delete comment
     */
    @Secured("ROLE_USER")
    @RequestMapping(value = "/articles/{post_id}/comments/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView deleteComment(@PathVariable int post_id, @PathVariable int id, HttpSession session) {
        int userId = userRepository.findByEmail((String) session.getAttribute("email")).getId();
        int postAuthorId = articleRepository.getOne(post_id).getAuthor_id();
        int commentAuthorId = commentRepository.getOne(id).getAuthor_id();

        System.out.println(userId + "  " + postAuthorId + "  " + commentAuthorId);

        if (userId == postAuthorId || userId == commentAuthorId) {
            commentRepository.deleteById(id);
        } else {
            System.out.println("no rights");
        }

        return new ModelAndView("redirect:/blog/articles/" + post_id + "/comments");
    }
}
