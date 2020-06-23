package com.victory.blog.base.comment;

import com.victory.blog.base.article.ArticleService;
import com.victory.blog.base.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.sql.Date;
import java.util.Calendar;

@Service
@Transactional
public class CommentService {

    @Inject
    private CommentRepository commentRepository;

    @Inject
    private UserService userService;

    @Inject
    private ArticleService articleService;

    public Page<Comment> getByPostId(Integer post_id, Pageable pageable) {

        return commentRepository.findByPostId(post_id, pageable);
    }

    public Comment createAndSave(Comment comment, String email, int post_id) {

        comment.setCreatedAt(new Date(Calendar.getInstance().getTime().getTime()));
        comment.setAuthorId(userService.getByEmail(email).getId());
        comment.setPostId(post_id);

        return commentRepository.save(comment);
    }

    public void deleteComment(int post_id, int id, String email) {
        int userId = userService.getByEmail(email).getId();
        int postAuthorId = articleService.getById(post_id).getAuthorId();
        int commentAuthorId = commentRepository.getOne(id).getAuthorId();

        System.out.println(userId + "  " + postAuthorId + "  " + commentAuthorId);

        if (userId == postAuthorId || userId == commentAuthorId) {
            commentRepository.deleteById(id);
        } else {
            System.out.println("no rights");
        }
    }

    public Page<Comment> filter(int skip, int limit, int author_id, String sortField, String order,
                                Pageable pageable) {
        Sort sort = Sort.by(sortField).ascending();

        if (order.equalsIgnoreCase("desc")) {
            sort = Sort.by(Sort.Order.desc(sortField));
        }

        return commentRepository.findAll(CommentSpecification.commentAuthorId(author_id), PageRequest.of(skip, limit, sort));
    }
}
