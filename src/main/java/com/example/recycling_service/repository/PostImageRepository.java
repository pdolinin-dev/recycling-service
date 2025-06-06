package com.example.recycling_service.repository;

import com.example.recycling_service.model.Post;
import com.example.recycling_service.model.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {

    PostImage save (PostImage postImage);

}
