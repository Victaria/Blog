package com.victory.blog.base.article;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "Article")
public class Article {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "title")
    private String title;

    @Column(name = "text")
    private String text;

    @Column(name = "status")
    private String status;

    @Column(name = "author_id")
   /* @ManyToOne
    @JoinColumn(name = "author_id")*/
    private Integer authorId;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updated_at) {
        this.updatedAt = updated_at;
    }

    public Article() {
    }

    public Article(String title, String text, String status, int author_id, Date created_at) {
        this.title = title;
        this.text = text;
        this.status = status;
        this.createdAt = created_at;
        this.authorId = author_id;
    }
}
