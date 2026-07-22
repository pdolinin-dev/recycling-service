package com.example.recycling_service.dto;

import lombok.Data;

import java.util.List;

@Data
public class PageResponse<T> {
    private List<T> content;
    private int pageSize;
    private int pageNumber;
    private long totalElements;
}
