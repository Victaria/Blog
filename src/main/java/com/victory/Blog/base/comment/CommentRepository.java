package com.victory.blog.base.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

public interface CommentRepository extends JpaRepository<Comment, Integer>, JpaSpecificationExecutor<Comment> {

    @Query("SELECT t FROM Comment t WHERE t.postId = :id ")
    Page<Comment> findByPostId(@Param("id") Integer post_id, Pageable pageable);

    void deleteById(@NonNull Integer id);
}
