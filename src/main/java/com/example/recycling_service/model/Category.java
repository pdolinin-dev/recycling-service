package com.example.recycling_service.model;

import jakarta.persistence.*;
import lombok.Getter;


@Getter
@Entity
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
}
