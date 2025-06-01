package com.example.recycling_service.controller;

import com.example.recycling_service.dto.PostFilterRequest;
import com.example.recycling_service.model.Post;
import com.example.recycling_service.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPost(@PathVariable Long id) {
        log.info("Fetching post with id: {}", id);
        Optional<Post> post = postService.getPostById(id);

        if (post.isPresent()) {
            log.info("Post found: {}", post.get());
            return ResponseEntity.ok(post.get());
        } else {
            log.warn("Post with id {} not found", id);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping()
    public ResponseEntity<List<Post>> getPosts(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String type
    ) {
        PostFilterRequest filter = new PostFilterRequest();
        filter.setStartDate(startDate);
        filter.setEndDate(endDate);
        filter.setType(type);

        List<Post> posts = postService.getFilteredPosts(filter);
        return ResponseEntity.ok(posts);
    }

    @PostMapping
    public Post createPost(@RequestBody Post post) {
        return postService.createPost(post);
    }

    @PutMapping("/{id}")
    public Post updatePost(@PathVariable Long id, @RequestBody Post post) {
        return postService.updatePost(id, post);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}
