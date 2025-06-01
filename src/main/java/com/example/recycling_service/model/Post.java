package com.example.recycling_service.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name="posts")
public class Post {
    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    private String title;

    @Getter
    @Setter
    @Column(columnDefinition = "TEXT")
    private String content; // Содержит HTML с тегами <img>

    @Getter
    @Setter
    private String author;

    @Getter
    @Setter
    @CreationTimestamp
    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate createdAt;

    @Getter
    @Setter
    @UpdateTimestamp
    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate updatedAt;

    @Getter
    @Setter
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImage> images = new ArrayList<>();

    @Getter
    @Setter
    @Column(name = "post_type")
    private String type;

    // Геттеры и сеттеры
}
