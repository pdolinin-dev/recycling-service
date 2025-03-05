/*  Data Transfer Objects
    Передача данных между слоями
*/
package com.example.recycling_service.dto;

import lombok.Data;

@Data
public class RecyclingPointDTO {
    private String name;
    private String type;
    private String address;
}
