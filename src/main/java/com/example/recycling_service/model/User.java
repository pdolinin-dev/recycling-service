package com.example.recycling_service.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class User {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true, name = "id")
    private Long id;

    @Getter
    @Setter
    @Column(nullable = false, unique = true, name = "user_login")
    private String username;

    @Getter
    @Column(nullable = false, name = "password_hash")
    private String password;

    @Getter
    @Setter
    @Column(nullable = false, unique = true)
    private String email;

    @Getter
    @Column(nullable = false, name = "user_name")
    private String name;

    @Getter
    @Column(nullable = false, name = "user_role")
    private String role = "PHYS"; // По умолчанию роль "phys"

    @Getter
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Getter
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}