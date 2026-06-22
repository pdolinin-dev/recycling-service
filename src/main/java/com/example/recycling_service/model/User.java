package com.example.recycling_service.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


import com.example.recycling_service.model.Enum.Role;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Table(name = "user")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true, name = "id")
    private UUID id;

    @Setter
    @Column(nullable = false, unique = true, name = "user_login")
    private String login;

    @Column(nullable = false, name = "password_hash")
    private String password;

    @Setter
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, name = "user_name")
    private String name;

    @Column(nullable = false, name = "user_role")
    private Role role; // По умолчанию роль "phys"

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Это нужно сделать через таблицу отдельную
//    @Column(name = "avatar_path")
//    private String avatarPath;
}