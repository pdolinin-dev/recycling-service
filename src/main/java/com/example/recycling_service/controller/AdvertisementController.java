package com.example.recycling_service.controller;

import com.example.recycling_service.dto.AdvertisementDTO;
import com.example.recycling_service.dto.CategoryRequest;
import com.example.recycling_service.dto.CreateAdvertisementRequest;
import com.example.recycling_service.dto.UpdateAdvertisementRequest;
import com.example.recycling_service.model.Advertisement;
import com.example.recycling_service.model.Category;
import com.example.recycling_service.model.PostImage;
import com.example.recycling_service.model.User;
import com.example.recycling_service.repository.AdvertisementRepository;
import com.example.recycling_service.repository.CategoryRepository;
import com.example.recycling_service.repository.UserRepository;
import com.example.recycling_service.service.AdvertisementService;
import com.example.recycling_service.service.ImageStorageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/advertisements")
@RequiredArgsConstructor
@Validated
public class AdvertisementController {
    private final AdvertisementService advertisementService;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final AdvertisementRepository advertisementRepository;
    private final ImageStorageService imageStorageService;

    @RestControllerAdvice
    public static class GlobalExceptionHandler {

        @ExceptionHandler(MethodArgumentNotValidException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
            Map<String, String> errors = new HashMap<>();
            ex.getBindingResult().getAllErrors().forEach(error -> {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errors.put(fieldName, errorMessage);
            });
            return ResponseEntity.badRequest().body(errors);
        }
    }

    /**
     * Получение объявлений по списку категорий.
     * @param request DTO с названиями категорий.
     * @return Список объявлений, относящихся к указанным категориям.
     */
    @PostMapping("/by-categories")
    public ResponseEntity<List<Advertisement>> getByCategories(
            @RequestBody List<Long> categoryIds) {
        List<Advertisement> ads = advertisementService.findByCategoryIds(categoryIds);
        return ResponseEntity.ok(ads);
    }


    //Delete advetrisement
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdvertisement(
            @PathVariable Long id,
            Authentication authentication) {
        log.info(authentication.getName());
        advertisementService.deleteAdvertisement(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/advertisements/{id}/images")
    public ResponseEntity<?> uploadAdImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            String fileName = imageStorageService.store(file);
            Advertisement ad = advertisementRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Ad not found"));

            PostImage image = new PostImage();
            image.setFilePath("/uploads/" + fileName);
            image.setMimeType(file.getContentType());
            ad.getImages().add(image);
            advertisementRepository.save(ad);

            return ResponseEntity.ok().body(image.getFilePath());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed: " + e.getMessage());
        }
    }




    //Change data in advertisement
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AdvertisementDTO> updateAdvertisement(@PathVariable Long id, @Valid @RequestBody UpdateAdvertisementRequest request) {
        AdvertisementDTO updatedAd = advertisementService.updateAdvertisement(id, request);
        return ResponseEntity.ok(updatedAd);
    }

    @GetMapping
    public ResponseEntity<List<AdvertisementDTO>> getAllAdvertisements() {
        List<Advertisement> ads = advertisementRepository.findAll(); // или через сервис
        List<AdvertisementDTO> result = ads.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    private AdvertisementDTO mapToDTO(Advertisement ad) {
        AdvertisementDTO dto = new AdvertisementDTO();
        dto.setId(ad.getId());
        dto.setTitle(ad.getTitle());
        dto.setDescription(ad.getDescription());
        dto.setPrice(ad.getPrice());
        dto.setAddress(ad.getAddress());
        dto.setCategories(ad.getCategories() != null ?
                ad.getCategories().stream()
                        .map(CategoryRequest::new)
                        .collect(Collectors.toSet()) :
                Collections.emptySet());
        dto.setUserId(ad.getUser().getId());
        dto.setCreatedAt(ad.getCreatedAt());

        // Добавляем пути к изображениям
        if (ad.getImages() != null) {
            List<String> imageUrls = ad.getImages().stream()
                    .map(image -> "http://localhost:8080" + image.getFilePath())
                    .collect(Collectors.toList());
            dto.setImageUrls(imageUrls);
        }

        return dto;
    }

    //add new advertisement
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AdvertisementDTO> createAdvertisement(
            @RequestPart("data") @Valid CreateAdvertisementRequest request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            Authentication authentication) throws IOException {

        AdvertisementDTO createdAd = advertisementService.createAdvertisementWithImages(
                request, files, authentication.getName());
        return ResponseEntity.ok(createdAd);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdvertisementDTO> getAdvertisementById(@PathVariable Long id) {
        Advertisement ad = advertisementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Объявление не найдено"));
        return ResponseEntity.ok(mapToDTO(ad));
    }


    // Добавляем обработчик исключений валидации
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest().body(errorMessage);
    }

    /**
    *   КАТЕГОРИИ
     */

    /**
     * Получение всех категорий
     * @return Список всех категорий
     */
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryRequest>> getAllCategories() {
        List<Category> categories = advertisementService.getAllCategories();
        List<CategoryRequest> requests = categories.stream().map(CategoryRequest::new).collect(Collectors.toList());
        return ResponseEntity.ok(requests);
    }

}
