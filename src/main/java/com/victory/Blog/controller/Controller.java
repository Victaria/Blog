package com.victory.Blog.controller;

import com.victory.Blog.base.article.Article;
import com.victory.Blog.base.article.ArticleRepository;
import com.victory.Blog.base.comment.CommentRepository;
import com.victory.Blog.base.user.User;
import com.victory.Blog.base.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.sql.Date;

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

    @Secured("ROLE_USER")
    @GetMapping(path = "/all")
    public @ResponseBody
    Iterable<User> getAllUsers() {
        // This returns a JSON or XML with the users
        return userRepository.findAll();
    }

    @Secured("ROLE_USER")
    @GetMapping(path = "/getUser")
    public @ResponseBody
    User getUserByEmail(@RequestParam("email") String email) {
        // This returns a JSON or XML with the user
        return userRepository.findByEmail(email);
    }

    @GetMapping(path = "/articles")
    public @ResponseBody
    ModelAndView getAllArticles(HttpSession session) {
        // This returns a JSON or XML with public articles

        ModelAndView mav = new ModelAndView("articles/main");;

        if (session.getAttribute("email") != null) {

            mav = new ModelAndView("afterAuth/main");

            mav.addObject("user", userRepository.findByEmail((String) session.getAttribute("email")));

        }

        mav.addObject("articles", articleRepository.findPublicArticles());

        return mav;
    }

    //localhost:8080/blog/articles/1/comments
    @GetMapping(path = "/articles/{post_id}/comments")
    public @ResponseBody
    ModelAndView getCommentsByPostId(@PathVariable("post_id") Integer post_id, HttpSession session) {
        // This returns a JSON or XML with the user
        ModelAndView mav = new ModelAndView("articles/comments");
        mav.addObject("comments", commentRepository.findByPostId(post_id));
        return mav;
    }

    @PostMapping("/articles")
    Article newArticle(@RequestBody Article article, HttpSession session) {
        return articleRepository.save(article);
    }

    @Secured("ROLE_USER")
    @GetMapping("/delete/{id}")
    public ModelAndView delete(@PathVariable Integer id, HttpSession session) {
        articleRepository.deleteById(id);
        return new ModelAndView("redirect:/blog/my");
    }

    // @PreAuthorize("hasRole('USER')")
    // @Secured("USER")
    @GetMapping("/my")
    public @ResponseBody
    ModelAndView showUserProfile(@NonNull Authentication authentication, HttpSession session) {
        authentication = SecurityContextHolder.getContext().getAuthentication();

        System.out.println(session.getAttribute("email"));

        if (session.getAttribute("email") != null) {
            ModelAndView mav = new ModelAndView("afterAuth/profile");
            mav.addObject("articles", articleRepository.findByAuthorId(userRepository.findByEmail((String) session.getAttribute("email")).getId()));
            mav.addObject("user", userRepository.findByEmail((String) session.getAttribute("email")));
            mav.addObject("templates",  articleRepository.findDraftByAuthorId(userRepository.findByEmail((String) session.getAttribute("email")).getId()));
            return mav;
        } else {
            return new ModelAndView("redirect:/blog/articles");
        }
    }

}
