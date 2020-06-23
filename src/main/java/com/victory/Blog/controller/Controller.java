package com.victory.blog.controller;

import com.victory.blog.base.article.Article;
import com.victory.blog.base.article.ArticleRequest;
import com.victory.blog.base.article.ArticleService;
import com.victory.blog.base.comment.Comment;
import com.victory.blog.base.comment.CommentService;
import com.victory.blog.base.tag.PostTagService;
import com.victory.blog.base.tag.TagService;
import com.victory.blog.base.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.Arrays;

@org.springframework.stereotype.Controller
@RequestMapping(path = "/blog")
public class Controller {

    @Inject
    private UserService userService;

    @Inject
    private ArticleService articleService;

    @Inject
    private TagService tagService;

    @Inject
    private PostTagService postTagService;

    @Inject
    private CommentService commentService;

    /**
     * Show all public articles
     */
    @RequestMapping(path = "/articles", method = RequestMethod.GET)
    public ModelAndView getAllArticles(HttpSession session,
                                       @PageableDefault(sort = {"id"},
                                               direction = Sort.Direction.DESC, value = 7) Pageable pageable) {

        ModelAndView mav = new ModelAndView("articles/main");

        if (session.getAttribute("email") != null) {

            mav = new ModelAndView("afterAuth/main");

            mav.addObject("user", userService.getByEmail((String) session.getAttribute("email")));
        }

        mav.addObject("articles", articleService.getPublicArticles(pageable));

        return mav;
    }

    /**
     * Get comments for post
     */
    @GetMapping(path = "/articles/{post_id}/comments")
    public ModelAndView getCommentsByPostId(@PathVariable int post_id, HttpSession session,
                                            @PageableDefault(sort = {"id"},
                                                    direction = Sort.Direction.DESC, value = 7) Pageable pageable) {

        ModelAndView mav = new ModelAndView("articles/comments");
        mav.addObject("comments", commentService.getByPostId(post_id, pageable));

        if (session.getAttribute("email") != null) {
            mav.addObject("user", userService.getByEmail((String) session.getAttribute("email")));
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

    @Transactional
    @RequestMapping(value = "/articles", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ModelAndView newArticle(@ModelAttribute ArticleRequest articleRequest, HttpSession session) {

        tagService.createTag(Arrays.asList(articleRequest.getTags().split(" ")), articleService.createArticle(articleRequest, (String) session.getAttribute("email")));

        return new ModelAndView("redirect:/blog/my");
    }

    /**
     * Show user profile
     */
    @GetMapping("/my")
    public ModelAndView showUserProfile(@NonNull HttpSession session,
                                        @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC, value = 5)
                                                Pageable pageable) {

        if (session.getAttribute("email") != null) {
            ModelAndView mav = new ModelAndView("afterAuth/profile", "articleRequest", new ArticleRequest());

            mav.addObject("articles", articleService.getByAuthorId(userService.getByEmail((String) session.getAttribute("email")).getId(), pageable));
            mav.addObject("user", userService.getByEmail((String) session.getAttribute("email")));

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
    public ModelAndView newComment(@PathVariable int id, @ModelAttribute Comment comment, HttpSession session) {

        commentService.createAndSave(comment, (String) session.getAttribute("email"), id);

        return new ModelAndView("redirect:/blog/articles/" + id + "/comments");
    }

    /**
     * Delete article
     */
    @Secured("ROLE_USER")
    @Transactional
    @RequestMapping(value = "/articles/{post_id}", method = RequestMethod.GET)
    public ModelAndView deleteArticle(@PathVariable int post_id, HttpSession session) {

        articleService.deleteArticle(post_id, (String) session.getAttribute("email"));

        return new ModelAndView("redirect:/blog/my");
    }

    /**
     * Delete comment
     */
    @Secured("ROLE_USER")
    @RequestMapping(value = "/articles/{post_id}/comments/{id}", method = RequestMethod.GET)
    public ModelAndView deleteComment(@PathVariable int post_id, @PathVariable int id, HttpSession session) {

        commentService.deleteComment(post_id, id, (String) session.getAttribute("email"));

        return new ModelAndView("redirect:/blog/articles/" + post_id + "/comments");
    }

    /**
     * Edit article
     */
    @RequestMapping(value = "/articles/{post_id}/edit", method = RequestMethod.GET)
    public ModelAndView editArticle(@PathVariable int post_id, HttpSession session,
                                    @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC)
                                            Pageable pageable) {

        int userId = userService.getByEmail((String) session.getAttribute("email")).getId();
        int postAuthorId = articleService.getById(post_id).getAuthorId();

        if (userId == postAuthorId) {
            ModelAndView mav = new ModelAndView("afterAuth/profile");
            mav.addObject("old_article", articleService.getById(post_id));

            mav.addObject("articles", articleService.getByAuthorId(postAuthorId, pageable));
            mav.addObject("user", userService.getById(userId));
            mav.addObject("templates", articleService.getDraftByAuthorId(postAuthorId));

            return mav;
        } else {
            return new ModelAndView("redirect:/blog/my");
        }
    }

    @Transactional
    @RequestMapping(value = "/articles/{post_id}/edit", method = RequestMethod.POST)
    public ModelAndView putArticle(@PathVariable int post_id, @ModelAttribute Article article) {

        articleService.updateArticle(post_id, article);

        return new ModelAndView("redirect:/blog/my");
    }

    @Transactional
    @RequestMapping(value = "/articles/{post_id}/change_status", method = RequestMethod.GET)
    public ModelAndView changeStatus(@PathVariable int post_id) {

        Article article = articleService.getById(post_id);

        if (article.getStatus().equals("draft")) {
            articleService.updateStatusToPublic(post_id);
        } else {
            articleService.updateStatusToDraft(post_id);
        }

        return new ModelAndView("redirect:/blog/my");
    }

    @RequestMapping(value = "/search/articles", method = RequestMethod.GET)
    public ModelAndView findByTags(@RequestParam("tags") String tags, HttpSession session,
                                   @PageableDefault(sort = {"id"},
                                           direction = Sort.Direction.DESC, value = 7) Pageable pageable) {

        Page<Article> articlePage = articleService.findArticleByTags(Arrays.asList(tags.split(",")), pageable);

        if (!articlePage.isEmpty()) {

            ModelAndView mav = new ModelAndView("articles/main");

            if (session.getAttribute("email") != null) {

                mav = new ModelAndView("afterAuth/main");

                mav.addObject("user", userService.getByEmail((String) session.getAttribute("email")));
            }

            mav.addObject("articles", articlePage);

            mav.getModelMap().addAttribute(pageable);


            return mav;
        }
        return new ModelAndView("info/information", "info", "No articles with such tags.");
    }

    @RequestMapping(value = "/tags-cloud", method = RequestMethod.GET)
    public ModelAndView getPostsCount(@RequestParam("tag") String tagName, HttpSession session,
                                      @PageableDefault(sort = {"id"},
                                              direction = Sort.Direction.DESC, value = 7) Pageable pageable) {
        ModelAndView mav = new ModelAndView("articles/main");

        if (session.getAttribute("email") != null) {

            mav = new ModelAndView("afterAuth/main");

            mav.addObject("user", userService.getByEmail((String) session.getAttribute("email")));
        }

        mav.addObject("articles", articleService.getPublicArticles(pageable));

        int count = postTagService.countArticlesWithTag(tagName);

        if (count > 0) {
            mav.addObject("count", "Count of articles with tag '" + tagName + "'" + " is " + count);
        } else {
            mav.addObject("count", "There is no posts with tag '" + tagName + "'");
        }
        return mav;
    }

    @RequestMapping(value = "/filter/articles", method = RequestMethod.GET)
    public ModelAndView getFilteredArticleList(@RequestParam("skip") int skip,
                                               @RequestParam("limit") int limit,
                                               @RequestParam("author_id") int author_id,
                                               @RequestParam("sort-field") String sortField,
                                               @RequestParam("order") String order,
                                               HttpSession session, @PageableDefault(value = 7) Pageable pageable) {

        ModelAndView mav = new ModelAndView("articles/main");

        if (session.getAttribute("email") != null) {

            mav = new ModelAndView("afterAuth/main");

            mav.addObject("user", userService.getByEmail((String) session.getAttribute("email")));
        }

        mav.addObject("articles", articleService.filter(skip, limit, author_id, sortField, order));

        return mav;

    }

    @RequestMapping(value = "/filter/articles/{id}/comments", method = RequestMethod.GET)
    public ModelAndView getFilteredCommentList(@RequestParam("skip") int skip,
                                               @RequestParam("limit") int limit,
                                               @RequestParam("author_id") int author_id,
                                               @RequestParam("sort-field") String sortField,
                                               @RequestParam("order") String order,
                                               @PathVariable int id,
                                               HttpSession session, @PageableDefault(value = 7) Pageable pageable) {

        ModelAndView mav = new ModelAndView("articles/comments");

        if (session.getAttribute("email") != null) {
            mav.addObject("user", userService.getByEmail((String) session.getAttribute("email")));
            mav.addObject("post_id", id);
            mav.addObject("new_comment", new Comment());
        } else {
            mav.addObject("user", null);
        }

        mav.addObject("comments", commentService.filter(skip, limit, author_id, sortField, order));

        return mav;
    }


}
