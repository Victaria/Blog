package com.victory.blog.base.tag;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class PostTagService {

    @Inject
    private PostTagRepository postTagRepository;

    @Inject
    private TagService tagService;

    public PostTag createPostTag(int post_id, int tag_id) {
        PostTag postTag = new PostTag();
        postTag.setTagId(tag_id);
        postTag.setPostId(post_id);
        return postTagRepository.save(postTag);
    }

    public List<PostTag> getAllByTagId(int tag_id) {
        return postTagRepository.findAllByTagId(tag_id);
    }

    public int countArticlesWithTag(String tagName) {
        Tag tag = tagService.getTag(tagName);
        Set<PostTag> postTagSet = new LinkedHashSet<>();

        if (tag != null) {
            postTagSet.addAll(postTagRepository.findAllByTagId(tag.getId()));
        }

        return postTagSet.size();
    }
}
