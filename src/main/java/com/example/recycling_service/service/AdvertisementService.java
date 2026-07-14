package com.example.recycling_service.service;

import com.example.recycling_service.dto.Response.AdvertisementResponse;
import com.example.recycling_service.dto.CategoryDto;
import com.example.recycling_service.dto.Request.CreateAdvertisementRequest;
import com.example.recycling_service.dto.Request.UpdateAdvertisementRequest;
import com.example.recycling_service.exception.NotFoundException;
import com.example.recycling_service.model.*;
import com.example.recycling_service.repository.AdvertisementRepository;
import com.example.recycling_service.repository.CategoryRepository;
import com.example.recycling_service.repository.MediaRepository;
import com.example.recycling_service.repository.UserRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Validated // Добавляем аннотацию для валидации на уровне сервиса
public class AdvertisementService {
    private final AdvertisementRepository advertisementRepository;
    @Autowired
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ImageStorageService imageStorageService;
    @Autowired
    private MediaRepository mediaRepository;


    // Find advertisement by id
    public AdvertisementResponse findAdvertisementById(UUID id) {
        return advertisementRepository.findById(id)
                .map(AdvertisementResponse::new)
                .orElseThrow(() -> new NotFoundException("Объявление", "id", id));
    }

    // Save advertisement
    public AdvertisementResponse addImage(UUID advertisementId, MultipartFile file) throws IOException {
        Advertisement advertisement = advertisementRepository.findById(advertisementId)
                .orElseThrow(() -> new NotFoundException("Объявление", "id", advertisementId));

        String fileName = imageStorageService.store(file);

        Media media = new Media();
        media.setFilePath("/uploads/" + fileName);
        media.setMimeType(file.getContentType());
        media.setName(file.getOriginalFilename());
        media.setSize((int) file.getSize());

        advertisement.getMedia().add(media);

        Advertisement savedAd = advertisementRepository.save(advertisement);

        return mapToDTO(savedAd);
    }

    // Delete advertisement
    public void deleteAdvertisement(UUID id, String userName) {
        User currentUser = userRepository.findByUsername(userName).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Advertisement ad = advertisementRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Объявление с ID " + id + " не найдено"
                ));

        // Проверяем, является ли текущий пользователь владельцем
        if (!ad.getUser().getId().equals(currentUser.getId()) && !currentUser.getRole().equals("admin")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Вы не можете удалить это объявление");
        }
        advertisementRepository.delete(ad);
    }

    /**
     * Поиск объявлений, относящихся к ЛЮБОЙ из указанных категорий.
     */
    public List<Advertisement> findByCategoryIds(List<UUID> categoryIds) {
        return advertisementRepository.findByCategoryIds(categoryIds);
    }

    // Find all advertisements
    public List<AdvertisementResponse> findAll() {

        List<Advertisement> ads = advertisementRepository.findAll();

        return ads.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Update Advertesiment
    public AdvertisementResponse updateAdvertisement(UUID id, @Valid UpdateAdvertisementRequest request) {
        Advertisement ad = advertisementRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Advertisement not found with id: " + id
                ));

        // Обновляем только переданные поля (если они не null)
        if (request.getTitle() != null) {
            ad.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            ad.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            ad.setPrice(request.getPrice());
        }
        if (request.getCategoryIds() != null) {
            Set<Category> categories = new HashSet<>(categoryRepository.findAllById(request.getCategoryIds()));
            ad.setCategories(categories);
        }


        Advertisement updatedAd = advertisementRepository.save(ad);
        return new AdvertisementResponse(updatedAd);
    }

    // get all advertisements
    public List<AdvertisementResponse> getAllAdvertisements() {
        return advertisementRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(AdvertisementResponse::new)
                .collect(Collectors.toList());
    }

    public AdvertisementResponse createAdvertisementWithImages(CreateAdvertisementRequest request, List<MultipartFile> files, String username) throws IOException {
        AdvertisementResponse created = createAdvertisement(request, username);

        if (files != null && !files.isEmpty()) {
            Advertisement ad = advertisementRepository.findById(created.getId())
                    .orElseThrow(() -> new RuntimeException("Объявление не найдено"));

            for (MultipartFile file : files) {
                String fileName = imageStorageService.store(file);
                Media media = new Media();
                media.setFilePath("/uploads/" + fileName);
                media.setMimeType(file.getContentType());
                ad.getMedia().add(media);
            }

            advertisementRepository.save(ad);
        }

        return created;
    }


    // create new advertisement
    public AdvertisementResponse createAdvertisement(CreateAdvertisementRequest request, String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Set<Category> categories = new HashSet<>(categoryRepository.findAllById(request.getCategoryIds()));

        if (categories.size() != request.getCategoryIds().size()) {
            throw new IllegalArgumentException("Some categories not found");
        }

        // Создаем объявление
        Advertisement advertisement = new Advertisement();
        advertisement.setTitle(request.getTitle());
        advertisement.setDescription(request.getDescription());
        advertisement.setPrice(request.getPrice());
        advertisement.setUser(user);
        advertisement.setCategories(categories);

        Advertisement savedAd = advertisementRepository.save(advertisement);
        return new AdvertisementResponse(savedAd);
    }

    // get advertisement by id
    public AdvertisementResponse getAdvertisementById(UUID id) {
        Advertisement advertisement = advertisementRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Advertisement not found with id: " + id
                ));
        return new AdvertisementResponse(advertisement);
    }

    /**
     * КАТЕГОРИИ
     */
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    private AdvertisementResponse mapToDTO(Advertisement ad) {

        AdvertisementResponse dto = new AdvertisementResponse();
        dto.setId(ad.getId());
        dto.setTitle(ad.getTitle());
        dto.setDescription(ad.getDescription());
        dto.setPrice(ad.getPrice());
        dto.setAddress(ad.getAddress());
        dto.setCategories(ad.getCategories() != null ?
                ad.getCategories().stream()
                .map(CategoryDto::new)
                .collect(Collectors.toSet()) :
                Collections.emptySet());
        dto.setUserId(ad.getUser().getId());
        dto.setCreatedAt(ad.getCreatedAt());

        // Добавляем пути к изображениям
        if (!ad.getMedia().isEmpty()) {
            List<String> mediaFilePaths = ad.getMedia().stream()
                    .map(image -> "http://localhost:8080" + image.getFilePath())
                    .collect(Collectors.toList());
            dto.setMediaFilePaths(mediaFilePaths);
        }

        return dto;
    }
}
