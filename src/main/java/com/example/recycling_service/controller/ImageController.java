package com.example.recycling_service.controller;

import com.example.recycling_service.model.Media;
import com.example.recycling_service.model.Post;
import com.example.recycling_service.repository.MediaRepository;
import com.example.recycling_service.repository.PostRepository;
import com.example.recycling_service.service.ImageStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/images")
public class ImageController {
    @Autowired
    private ImageStorageService imageStorageService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MediaRepository mediaRepository;

    @Value("${upload.path}")
    private String uploadPath;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String filename = imageStorageService.store(file);
            return ResponseEntity.ok("/uploads/" + filename);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/upload/post")
    public ResponseEntity<String> uploadPostImage(@RequestParam("file") MultipartFile file,
                                                  @RequestParam("postId") UUID postId) {
        try {
            String filename = imageStorageService.store(file);
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new IllegalArgumentException("Post not found"));

            Media media = new Media();
            media.setFilePath("/uploads/" + filename);

//          Добавить добавления медиа к посту
//            media.setPost(post);

            mediaRepository.save(media);

            return ResponseEntity.ok("/uploads/" + filename);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при сохранении файла");
        }
    }

    @PostMapping("/upload-avatar")
    public ResponseEntity<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        try {
            String filename = imageStorageService.store(file);
            return ResponseEntity.ok("/uploads/" + filename);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}
