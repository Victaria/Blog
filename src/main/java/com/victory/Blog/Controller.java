package com.victory.Blog;

import com.victory.Blog.base.article.Article;
import com.victory.Blog.base.article.ArticleRepository;
import com.victory.Blog.base.comment.Comment;
import com.victory.Blog.base.comment.CommentRepository;
import com.victory.Blog.base.user.User;
import com.victory.Blog.base.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
        user.setCreated_at(Date.valueOf("1995-12-11"));
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
    List<Article> getAllArticles() {
        return articleRepository.findPublicArticles();
    }

    //localhost:8080/blog/articles/1/comments
    @GetMapping(path = "/articles/{post_id}/comments")
    public @ResponseBody
    List<Comment> getCommentsByPostId(@PathVariable("post_id") Integer post_id) {
        // This returns a JSON or XML with the user
        return commentRepository.findByPostId(post_id);
    }

   /* @GetMapping(value = "/main")
    public  String  getMainPage(Model model) {
        // This returns a JSON or XML with public articles
        List<Article> articles = new ArrayList<>();
        for (Article article : articleRepository.findAll()){
            if (article.getStatus().equalsIgnoreCase("public")){
                articles.add(article);
            }
        }
          model.addAttribute("articles", articles);
        return "main";
    }*/
}
