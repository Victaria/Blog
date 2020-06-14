package com.victory.Blog.base.comment;

import com.victory.Blog.base.article.ArticleService;
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
import java.util.Calendar;
import java.util.List;

@Service
@Transactional
public class CommentService {
    @Autowired
    CommentRepository commentRepository;

    @Autowired
    UserService userService;

    @Autowired
    ArticleService articleService;

    public Page<Comment> getByPostId(Integer post_id, Pageable pageable) {

        return commentRepository.findByPostId(post_id, pageable);
    }

    public Comment createAndSave(Comment comment, HttpSession session, int post_id) {
        comment.setCreatedAt(new Date(Calendar.getInstance().getTime().getTime()));
        comment.setAuthorId(userService.getByEmail((String) session.getAttribute("email")).getId());
        comment.setPostId(post_id);

        return commentRepository.save(comment);
    }

    public HttpStatus deleteComment(int post_id, int id, HttpSession session) {
        int userId = userService.getByEmail((String) session.getAttribute("email")).getId();
        int postAuthorId = articleService.getById(post_id).getAuthorId();
        int commentAuthorId = commentRepository.getOne(id).getAuthorId();

        System.out.println(userId + "  " + postAuthorId + "  " + commentAuthorId);

        if (userId == postAuthorId || userId == commentAuthorId) {
            commentRepository.deleteById(id);
            return HttpStatus.OK;
        } else {
            System.out.println("no rights");
            return HttpStatus.NOT_IMPLEMENTED;
        }
    }

    public Page<Comment> filter(int skip, int limit, int author_id, String sortField, String order,
                                Pageable pageable) {
        Sort sort = Sort.by(sortField).ascending();

        if (order.equalsIgnoreCase("desc")) {
            sort = Sort.by(Sort.Order.desc(sortField));
        }

        List<Comment> comments = commentRepository.findAll(CommentSpecification.commentAuthorId(author_id), sort);

        return new PageImpl<Comment>(comments, pageable, comments.size());
    }
}
