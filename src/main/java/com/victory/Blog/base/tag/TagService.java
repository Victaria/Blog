package com.victory.blog.base.tag;

import com.victory.blog.base.article.Article;
import com.victory.blog.base.article.ArticleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;

@Service
@Transactional
public class TagService {

    @Inject
    private TagRepository tagRepository;

    @Inject
    private PostTagService postTagService;

    @Inject
    private ArticleService articleService;

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
