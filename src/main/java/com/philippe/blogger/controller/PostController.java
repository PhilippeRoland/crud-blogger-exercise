package com.philippe.blogger.controller;

import com.philippe.blogger.entity.Post;
import com.philippe.blogger.entity.Tag;
import com.philippe.blogger.repository.PostRepository;
import com.philippe.blogger.repository.TagRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
* Main Controller class.
* Conventional wisdom would involve having a service layer containing the business logic.
* This seems redundant and overly verbose with the functionality as-is.
* I'd only recommend introducing it once functionality goes beyond simple CRUD - or CR in this case
* */
@Controller
@RequestMapping("/posts")
public class PostController {

    Logger logger = LoggerFactory.getLogger(PostController.class);

    @Autowired
    PostRepository postRepository;
    @Autowired
    TagRepository tagRepository;

    @PostMapping("")
    public ResponseEntity<Object> createPost(@RequestBody Post post) {
        logger.info(String.format("Creating post: " + post.toString()));
        try {
            /*
            Setting the cascadeType to all in the Post entity
            causes the .save below this to attempt recreating tags that already exist
            Instead, manually create them as needed and set cascade type to NONE (the default)
            */
            Set<Tag> tags = post.getTags();
            if(tags != null) {
                tags.stream().forEach(tag -> createIfNotPresent(tag));
            }
            postRepository.save(post);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Sorting: By default, sort by date DESC, even if sort is empty
    //TODO: allow ordering by multiple properties at once, in different directions (author asc, date desc)
    @GetMapping("")
    public ResponseEntity<Object> findAllSortable(
            @RequestParam(defaultValue = "creationDate", required = false) String sort_by,
            @RequestParam(defaultValue = "desc", required = false) String order,
            @RequestParam(required = false) String tag_filter) {
        logger.info(String.format("Retrieving posts: sort_by %s, desc %s, tag_filter %s", sort_by, order, tag_filter));
        List<Post> sortedList;
        try {
            Sort.Order sortDirection = getSortDirection(order, sort_by);
            sortedList = postRepository.findAll(Sort.by(sortDirection));
            sortedList = StringUtils.isBlank(tag_filter)? sortedList = sortedList:
                    sortedList.stream().
                    filter(post -> post.hasTag(tag_filter)).
                    collect(Collectors.toList());
        } catch(PropertyReferenceException e) {
            //Will happen if sort_by refers to non-existing property
            return ResponseEntity.badRequest().body(String.format("sort_by property '" + e.getPropertyName() + "' is unknown"));
        }
        return ResponseEntity.ok().body(sortedList);
    }

    private Sort.Order getSortDirection(String sortDirection, String propertyName) {
        Sort.Direction direction = StringUtils.startsWithIgnoreCase(sortDirection, "desc")?Sort.Direction.DESC:Sort.Direction.ASC;
        Sort.Order order = new Sort.Order(direction, propertyName);
        return order;
    }

    private void createIfNotPresent(Tag tag) {
        //findByName returns a list, but we should at most have one
        List<Tag> persistedTag = tagRepository.findByName(tag.getName());
        if (CollectionUtils.isEmpty(persistedTag)) {
            logger.info(String.format("Creating tag: " + tag.toString()));
            tagRepository.save(tag);
        }
    }


}
