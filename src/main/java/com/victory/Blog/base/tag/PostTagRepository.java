package com.victory.Blog.base.tag;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostTagRepository extends JpaRepository<PostTag, Integer> {
    List<PostTag> findAllByTagId(Integer tagId);
}
