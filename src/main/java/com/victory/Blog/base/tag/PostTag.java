package com.victory.Blog.base.tag;

import javax.persistence.*;

@Entity
@Table(name = "Post_Tag")
public class PostTag {
    @Column(name = "post_id")
    private Integer postId;

    @Id
    @Column(name = "tag_id")
    private Integer tagId;

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    public Integer getTagId() {
        return tagId;
    }

    public void setTagId(Integer tagId) {
        this.tagId = tagId;
    }
}
