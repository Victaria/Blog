package com.victory.Blog.base.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    @Query("SELECT t FROM Comment t WHERE t.post_id = ?1 ")
    List<Comment> findByPostId(Integer post_id);

}
