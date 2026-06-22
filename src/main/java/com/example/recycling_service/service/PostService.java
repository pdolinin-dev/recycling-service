package com.example.recycling_service.service;

import com.example.recycling_service.dto.Request.PostFilterRequest;
import com.example.recycling_service.model.Post;
import com.example.recycling_service.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;


    public Post savePost(Post post) {
        // Обработка контента перед сохранением
        String processedContent = processContent(post.getContent());
        post.setContent(processedContent);
        return postRepository.save(post);
    }

    private String processContent(String content) {
        // Здесь можно добавить обработку контента
        return content;
    }

    public Optional<Post> getPostById(Long id) {
        return postRepository.findPostById(id);
    }

    public List<Post> getFilteredPosts(PostFilterRequest filter) {
        LocalDateTime startDateTime = filter.getStartDate() != null ?
                filter.getStartDate().atStartOfDay() : null;

        LocalDateTime endDateTime = filter.getEndDate() != null ?
                filter.getEndDate().atTime(LocalTime.MAX) : null;

        return postRepository.findFilteredPosts(
                filter.getType(),
                startDateTime,
                endDateTime
        );
    }

    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    public Post updatePost(Long id, Post updatedPost) {
        Post existing = postRepository.findById(id).orElseThrow();
        existing.setTitle(updatedPost.getTitle());
        existing.setContent(updatedPost.getContent());
        existing.setAuthor(updatedPost.getAuthor());
        existing.setType(updatedPost.getType());
        return postRepository.save(existing);
    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }
}
