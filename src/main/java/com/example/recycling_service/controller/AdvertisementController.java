package com.example.recycling_service.controller;

import com.example.recycling_service.dto.AdvertisementDTO;
import com.example.recycling_service.dto.CategoryRequest;
import com.example.recycling_service.dto.CreateAdvertisementRequest;
import com.example.recycling_service.dto.UpdateAdvertisementRequest;
import com.example.recycling_service.model.Advertisement;
import com.example.recycling_service.model.Category;
import com.example.recycling_service.model.User;
import com.example.recycling_service.repository.CategoryRepository;
import com.example.recycling_service.repository.UserRepository;
import com.example.recycling_service.service.AdvertisementService;
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

    //Change data in advertisement
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AdvertisementDTO> updateAdvertisement(@PathVariable Long id, @Valid @RequestBody UpdateAdvertisementRequest request) {
        AdvertisementDTO updatedAd = advertisementService.updateAdvertisement(id, request);
        return ResponseEntity.ok(updatedAd);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdvertisementDTO> getAdvertisementById(@PathVariable Long id) {
        AdvertisementDTO advertisementDTO = advertisementService.getAdvertisementById(id);
        return ResponseEntity.ok(advertisementDTO);
    }

    //Get advertisements
    @GetMapping
    public ResponseEntity<List<AdvertisementDTO>> getAllAdvertisements() {
        return ResponseEntity.ok(advertisementService.getAllAdvertisements());
    }
    //add new advertisement
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AdvertisementDTO> createAdvertisement(
            @Valid @RequestBody CreateAdvertisementRequest request,
            Authentication authentication) {
        AdvertisementDTO createdAd = advertisementService.createAdvertisement(request, authentication.getName());
        return ResponseEntity.ok(createdAd);
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
