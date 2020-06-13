package com.victory.Blog.controller;

import com.victory.Blog.base.article.Article;
import com.victory.Blog.base.article.ArticleRepository;
import com.victory.Blog.base.article.ArticleRequest;
import com.victory.Blog.base.article.ArticleSpecification;
import com.victory.Blog.base.comment.Comment;
import com.victory.Blog.base.comment.CommentRepository;
import com.victory.Blog.base.tag.PostTag;
import com.victory.Blog.base.tag.PostTagRepository;
import com.victory.Blog.base.tag.Tag;
import com.victory.Blog.base.tag.TagRepository;
import com.victory.Blog.base.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.sql.Date;
import java.util.*;

@org.springframework.stereotype.Controller
@RequestMapping(path = "/blog")
public class Controller {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private PostTagRepository postTagRepository;

    /**
     * Show all public articles
     */
    @RequestMapping(path = "/articles", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView getAllArticles(HttpSession session,
                                @PageableDefault(sort = {"id"},
                                        direction = Sort.Direction.DESC, value = 7) Pageable pageable) {

        ModelAndView mav = new ModelAndView("articles/main");

        if (session.getAttribute("email") != null) {

            mav = new ModelAndView("afterAuth/main");

            mav.addObject("user", userRepository.findByEmail((String) session.getAttribute("email")));
        }

        Page<Article> page = articleRepository.findPublicArticles(pageable);

        mav.addObject("articles", page);

        return mav;
    }

    /**
     * Get comments for post
     */
    @GetMapping(path = "/articles/{post_id}/comments")
    public @ResponseBody
    ModelAndView getCommentsByPostId(@PathVariable int post_id, HttpSession session,
                                     @PageableDefault(sort = {"id"},
                                             direction = Sort.Direction.DESC, value = 7) Pageable pageable) {

        ModelAndView mav = new ModelAndView("articles/comments");
        mav.addObject("comments", commentRepository.findByPostId(post_id, pageable));

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

        List<String> tags = Arrays.asList(articleRequest.getTags().split(" "));

        Article article = new Article();
        article.setAuthor_id(userRepository.findByEmail((String) session.getAttribute("email")).getId());
        article.setText(articleRequest.getText());
        article.setTitle(articleRequest.getTitle());
        article.setStatus("public");
        article.setCreated_at(new Date(Calendar.getInstance().getTime().getTime()));

        articleRepository.save(article);

        for (String tag : tags) {
            Tag existTag = tagRepository.findByName(tag);
            PostTag pt = new PostTag();

            if (existTag != null) {
                pt.setPostId(articleRepository.findByTitleAndAuthorId(article.getTitle(), article.getAuthor_id()).getId());
                pt.setTagId(existTag.getId());

            } else {
                Tag tagObj = new Tag();
                tagObj.setName(tag);
                tagRepository.save(tagObj);

                pt.setPostId(articleRepository.findByTitleAndAuthorId(article.getTitle(), article.getAuthor_id()).getId());
                pt.setTagId(tagRepository.findByName(tag).getId());
            }
            postTagRepository.save(pt);
        }

        System.out.println("title: " + articleRequest.getTitle() + "text: " + articleRequest.getText());

        return new ModelAndView("redirect:/blog/my");
    }

    /**
     * Show user profile
     */
    @GetMapping("/my")
    public @ResponseBody
    ModelAndView showUserProfile(@NonNull HttpSession session, @PageableDefault(sort = {"id"},
            direction = Sort.Direction.DESC, value = 5)
            Pageable pageable) {

        System.out.println(session.getAttribute("email"));

        if (session.getAttribute("email") != null) {
            ModelAndView mav = new ModelAndView("afterAuth/profile", "articleRequest", new ArticleRequest());

            mav.addObject("articles", articleRepository.findByAuthorId(userRepository.findByEmail((String) session.getAttribute("email")).getId(), pageable));
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

    /**
     * Edit article
     */
    @RequestMapping(value = "/articles/{post_id}/edit", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView editArticle(@PathVariable int post_id, HttpSession session,
                             @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC)
                                     Pageable pageable) {
        int userId = userRepository.findByEmail((String) session.getAttribute("email")).getId();
        int postAuthorId = articleRepository.getOne(post_id).getAuthor_id();

        if (userId == postAuthorId) {
            ModelAndView mav = new ModelAndView("afterAuth/profile");
            mav.addObject("old_article", articleRepository.getOne(post_id));

            mav.addObject("articles", articleRepository.findByAuthorId(postAuthorId, pageable));
            mav.addObject("user", userRepository.findByEmail((String) session.getAttribute("email")));
            mav.addObject("templates", articleRepository.findDraftByAuthorId(postAuthorId));

            return mav;
        } else {
            System.out.println("no rights");
            return new ModelAndView("redirect:/blog/my");
        }
    }

    @Transactional
    @RequestMapping(value = "/articles/{post_id}/edit", method = RequestMethod.POST)
    public @ResponseBody
    ModelAndView putArticle(@PathVariable int post_id, @ModelAttribute Article article) {
        article.setUpdated_at(new Date(Calendar.getInstance().getTime().getTime()));

        articleRepository.updateArticle(article.getUpdated_at(), article.getTitle(), article.getText(), post_id);

        return new ModelAndView("redirect:/blog/my");
    }

    @RequestMapping(value = "/search/articles", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView findByTags(@RequestParam("tags") String tags, HttpSession session,
                            @PageableDefault(sort = {"id"},
                                    direction = Sort.Direction.DESC, value = 7) Pageable pageable) {
        List<String> tagList = Arrays.asList(tags.split(","));
        Set<PostTag> postTagSet = new LinkedHashSet<>();
        Set<Article> articleSet = new LinkedHashSet<>();
        Tag tag;
        for (String tagName : tagList) {

            tag = tagRepository.findByName(tagName);
            if (tag != null) {
                postTagSet.addAll(postTagRepository.findAllByTagId(tag.getId()));
            }
            System.out.println(postTagSet.toString());
            if (!postTagSet.isEmpty()) {
                ModelAndView mav = new ModelAndView("articles/main");

                for (PostTag postTag : postTagSet) {
                    articleSet.add(articleRepository.findById(postTag.getPostId()).get());
                }

                Page<Article> articlePage = new PageImpl<Article>(new ArrayList<>(articleSet), pageable, articleSet.size());

                System.out.println(session.getAttribute("email"));

                if (session.getAttribute("email") != null) {
                    System.out.println("In after auth ****** *******");

                    mav = new ModelAndView("afterAuth/main");

                    mav.addObject("user", userRepository.findByEmail((String) session.getAttribute("email")));
                }

                mav.addObject("articles", articlePage);

                mav.getModelMap().addAttribute(pageable);

                return mav;
            }
        }
        return new ModelAndView("info/information", "info", "No articles with such tags.");
    }

    @RequestMapping(value = "/tags-cloud", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView getPostsCount(@RequestParam("tag") String tagName, HttpSession session,
                               @PageableDefault(sort = {"id"},
                                       direction = Sort.Direction.DESC, value = 7) Pageable pageable) {
        ModelAndView mav = new ModelAndView("articles/main");

        if (session.getAttribute("email") != null) {

            mav = new ModelAndView("afterAuth/main");

            mav.addObject("user", userRepository.findByEmail((String) session.getAttribute("email")));
        }

        mav.addObject("articles", articleRepository.findPublicArticles(pageable));

        Tag tag = tagRepository.findByName(tagName);
        Set<PostTag> postTagSet = new LinkedHashSet<>();

        if (tag != null) {
            postTagSet.addAll(postTagRepository.findAllByTagId(tag.getId()));
        }
        if (!postTagSet.isEmpty()) {
            int count = postTagSet.size();

            mav.addObject("count", "Count of articles with tag '" + tagName + "'" + " is " + count);
        } else {
            mav.addObject("count", "There is no posts with tag '" + tagName + "'");
        }
        return mav;
    }

    @RequestMapping(value = "/filter/articles", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView getFilteredArticleList(@RequestParam("skip") int skip,
                                        @RequestParam("limit") int limit,
                                        @RequestParam("author_id") int author_id,
                                        @RequestParam("sort-field") String sortField,
                                        @RequestParam("order") String order,
                                        HttpSession session, @PageableDefault(value = 7) Pageable pageable) {
        Sort sort = Sort.by(sortField).ascending();

        if (order.equalsIgnoreCase("desc")) {
            sort = Sort.by(sortField).descending();
        }

        List<Article> articles = articleRepository.findAll(ArticleSpecification.postAuthorId(author_id), sort);

        for (Article article : articles) {
            System.out.println(article.getTitle());
        }

        ModelAndView mav = new ModelAndView("articles/main");

        if (session.getAttribute("email") != null) {

            mav = new ModelAndView("afterAuth/main");

            mav.addObject("user", userRepository.findByEmail((String) session.getAttribute("email")));
        }

        mav.addObject("articles", new PageImpl<Article>(articles, pageable, articles.size()));

        return mav;

    }
}
