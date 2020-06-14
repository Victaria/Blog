package com.victory.Blog.base.article;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class ArticleSpecification {
    public static Specification<Article> postHasTitle(String title) {
        return new Specification<Article>() {
            @Override
            public Predicate toPredicate(Root<Article> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Specification<Article> spec = ArticleSpecification.postHasTitle(title);
                return criteriaBuilder.equal(root.get("title"), title);
            }
        };
    }

    public static Specification<Article> postAuthorId(int id) {
        return new Specification<Article>() {
            @Override
            public Predicate toPredicate(Root<Article> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.get("authorId"), id);
            }
        };
    }

}
