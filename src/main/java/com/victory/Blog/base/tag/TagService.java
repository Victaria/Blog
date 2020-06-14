package com.victory.Blog.base.tag;

import com.victory.Blog.base.article.Article;
import com.victory.Blog.base.article.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TagService {

    @Autowired
    TagRepository tagRepository;

    @Autowired
    PostTagService postTagService;

    @Autowired
    ArticleService articleService;

    public void createTag(List<String> tags, Article article) {

        for (String tag : tags) {
            Tag existTag = tagRepository.findByName(tag);
            int post_id;
            int tag_id;

            if (existTag != null) {
                post_id = articleService.getArticleByTitleAndAuthorId(article.getTitle(), article.getAuthorId()).getId();
                tag_id = existTag.getId();

            } else {
                Tag tagObj = new Tag();
                tagObj.setName(tag);
                tagRepository.save(tagObj);

                post_id = articleService.getArticleByTitleAndAuthorId(article.getTitle(), article.getAuthorId()).getId();
                tag_id = tagRepository.findByName(tag).getId();
            }
            postTagService.createPostTag(post_id, tag_id);
        }
    }

    public Tag getTag(String tagName) {
        return tagRepository.findByName(tagName);
    }

}
