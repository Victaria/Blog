package com.victory.blog.base.comment;

import com.victory.blog.base.article.Article;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;

public class CommentSpecification {

    public static Specification<Comment> postHasTitle(String title) {
        return new Specification<Comment>() {
            @Override
            public Predicate toPredicate(Root<Comment> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Specification<Comment> spec = CommentSpecification.postHasTitle(title);
                Join<Comment, Article> article = root.join("postId");

                return criteriaBuilder.equal(root.get("article").get("title"), title);
            }
        };
    }

    public static Specification<Comment> commentAuthorId(int id) {
        return new Specification<Comment>() {
            @Override
            public Predicate toPredicate(Root<Comment> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.get("authorId"), id);
            }
        };
    }
}
