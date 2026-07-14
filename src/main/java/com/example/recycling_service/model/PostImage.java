//package com.example.recycling_service.model;
//
//import com.example.recycling_service.model.Advertisement;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.Setter;
//
//@Getter
//@Setter
//@Entity
//@Table(name = "media_files")
//public class PostImage {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(name="file_path")
//    private String filePath;
//
//    @JsonIgnore
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "post_id")
//    private Post post;
//
//    private String mimeType;
//
//    @JsonIgnore
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "advertisement_id")
//    private Advertisement advertisement;
//}
