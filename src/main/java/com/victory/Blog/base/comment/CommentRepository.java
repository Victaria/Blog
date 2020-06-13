package com.victory.Blog.base.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    @Query("SELECT t FROM Comment t WHERE t.post_id = ?1 ")
    Page<Comment> findByPostId(Integer post_id, Pageable pageable);

    void deleteById(Integer integer);
}
