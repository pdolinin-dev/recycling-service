/*  Модель
    Представляет сущности базы данных
*/
package com.example.recycling_service.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class RecyclingPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type;
    private String address;
}
