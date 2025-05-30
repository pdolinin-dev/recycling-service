package com.example.recycling_service.model;

import jakarta.persistence.*;

@Entity
@Table(name = "media_files")
public class PostImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="file_path")
    private String filePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    // Геттеры и сеттеры
}
