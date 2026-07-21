package com.example.recycling_service.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

import java.util.UUID;

@Data
@Entity
@Table(name = "recycling_point_type")
public class Type {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
}
