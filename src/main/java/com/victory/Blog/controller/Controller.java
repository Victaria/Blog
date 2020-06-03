package com.victory.Blog.controller;

import com.victory.Blog.base.article.Article;
import com.victory.Blog.base.article.ArticleRepository;
import com.victory.Blog.base.comment.Comment;
import com.victory.Blog.base.comment.CommentRepository;
import com.victory.Blog.base.user.User;
import com.victory.Blog.base.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Date;
import java.util.List;

@org.springframework.stereotype.Controller
@RequestMapping(path = "/blog")
public class Controller {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private CommentRepository commentRepository;

    @PostMapping(path = "/add") // Map ONLY POST Requests
    public @ResponseBody
    String addNewUser(@RequestParam String first_name
            , @RequestParam String email, @RequestParam String last_name
            , @RequestParam Date created_at) {
        User user = new User();
        user.setFirstname(first_name);
        user.setEmail(email);
        user.setLastname(last_name);
        user.setCreated_at(Date.valueOf("2019-12-11"));
        userRepository.save(user);
        return "Saved";
    }

    @GetMapping(path = "/all")
    public @ResponseBody
    Iterable<User> getAllUsers() {
        // This returns a JSON or XML with the users
        return userRepository.findAll();
    }

    @GetMapping(path = "/getUser")
    public @ResponseBody
    User getUserByEmail(@RequestParam("email") String email) {
        // This returns a JSON or XML with the user
        return userRepository.findByEmail(email);
    }

    @GetMapping(path = "/articles")
    public @ResponseBody
    ModelAndView getAllArticles() {
        // This returns a JSON or XML with public articles
        ModelAndView mav = new ModelAndView("articles/main");
        mav.addObject("articles", articleRepository.findPublicArticles());
        return mav;
    }

    //localhost:8080/blog/articles/1/comments
    @GetMapping(path = "/articles/{post_id}/comments")
    public @ResponseBody
    ModelAndView getCommentsByPostId(@PathVariable("post_id") Integer post_id) {
        // This returns a JSON or XML with the user
        ModelAndView mav = new ModelAndView("articles/comments");
        mav.addObject("comments", commentRepository.findByPostId(post_id));
        return mav;
    }

    @PostMapping("/articles")
    Article newArticle(@RequestBody Article article) {
        return articleRepository.save(article);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        articleRepository.deleteById(id);
        return "";
    }

}
