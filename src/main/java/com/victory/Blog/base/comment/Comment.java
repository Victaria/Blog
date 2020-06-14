package com.victory.Blog.base.comment;

import com.victory.Blog.base.article.Article;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "Comment")
public class Comment {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "message")
    private String message;

    @Column(name = "post_id")
    private Integer postId;

    @Column(name = "author_id")
    private Integer authorId;

    @Column(name = "created_at")
    private Date createdAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer post_id) {
        this.postId = post_id;
    }

    public Integer getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Integer author_id) {
        this.authorId = author_id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date created_at) {
        this.createdAt = created_at;
    }
}
