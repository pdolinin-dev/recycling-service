package com.example.recycling_service.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Getter
@Setter
@Table(name="post")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String title;

    private String content;

    private String author;

    @CreationTimestamp
    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate createdAt;

    @UpdateTimestamp
    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate updatedAt;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Media> media = new HashSet<>();
}
