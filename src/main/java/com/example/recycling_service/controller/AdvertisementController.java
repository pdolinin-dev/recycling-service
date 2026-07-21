package com.example.recycling_service.controller;

import com.example.recycling_service.dto.Response.AdvertisementResponse;
import com.example.recycling_service.dto.CategoryDto;
import com.example.recycling_service.dto.Request.CreateAdvertisementRequest;
import com.example.recycling_service.dto.Request.UpdateAdvertisementRequest;
import com.example.recycling_service.model.Advertisement;
import com.example.recycling_service.model.Category;
import com.example.recycling_service.service.AdvertisementService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


//@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/v1/advertisements")
@RequiredArgsConstructor
@Validated
public class AdvertisementController {

    private final AdvertisementService advertisementService;

    /**
     * Get advertisements by categories
     * @param categoryIds List of category ids
     * @return List of advertisements
     */
    @PostMapping("/by-categories")
    public ResponseEntity<List<Advertisement>> getByCategories(
            @RequestBody List<UUID> categoryIds) {
        return ResponseEntity.ok(advertisementService.findByCategoryIds(categoryIds));
    }

    /**
     * Delete advertisement
     * @param id Advertisement id
     * @param authentication Authentication
     * @return Void
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdvertisement(
            @PathVariable UUID id,
            Authentication authentication) {
        advertisementService.deleteAdvertisement(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    /**
     * Upload image to advertisement
     * @param id Advertisement id
     * @param file MultipartFile
     * @return AdvertisementResponse
     * @throws IOException
     */
    @PostMapping("/{id}/images")
    public ResponseEntity<AdvertisementResponse> uploadAdImage(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        return ResponseEntity.ok(advertisementService.addImage(id, file));
    }

    /**
     * Update advertisement
     * @param id Advertisement id
     * @param authentication Authentication
     * @param request AdvertisementRequest
     * @return AdvertisementResponse
     */
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AdvertisementResponse> updateAdvertisement(@PathVariable UUID id,
                                                                     @Valid @RequestBody UpdateAdvertisementRequest request,
                                                                    Authentication authentication) {
        return ResponseEntity.ok(advertisementService.updateAdvertisement(id, request, authentication.getName()));
    }

    /**
     * Get all advertisements
     * @return
     */
    @GetMapping
    public ResponseEntity<List<AdvertisementResponse>> getAllAdvertisements() {
        return ResponseEntity.ok(advertisementService.findAll());
    }

    /**
     *
     * @param request AdvertisementRequest
     * @param files MultipartFiles
     * @param authentication Authentication
     * @return AdvertisementResponse
     * @throws IOException
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AdvertisementResponse> createAdvertisement(
            @RequestPart("data") @Valid CreateAdvertisementRequest request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            Authentication authentication) throws IOException {

        return ResponseEntity.ok(advertisementService.createAdvertisementWithImages(
                request, files, authentication.getName()));
    }

    /**
     *
     * @param id Advertisement id
     * @return Advertisement
     */
    @GetMapping("/{id}")
    public ResponseEntity<AdvertisementResponse> getAdvertisementById(@PathVariable UUID id) {
        return ResponseEntity.ok(advertisementService.findAdvertisementById(id));
    }

    /**
     * Получение всех категорий
     * @return Список всех категорий
     */
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        List<Category> categories = advertisementService.getAllCategories();
        List<CategoryDto> requests = categories.stream().map(CategoryDto::new).collect(Collectors.toList());

        return ResponseEntity.ok(requests);
    }
}
