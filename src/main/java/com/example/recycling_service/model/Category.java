package com.example.recycling_service.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.UUID;


@Getter
@Entity
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
}
