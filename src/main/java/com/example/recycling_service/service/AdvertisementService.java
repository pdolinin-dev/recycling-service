package com.example.recycling_service.service;

import com.example.recycling_service.dto.PageResponse;
import com.example.recycling_service.dto.Request.FilterAdvertisementRequest;
import com.example.recycling_service.dto.Response.AdvertisementResponse;
import com.example.recycling_service.dto.CategoryDto;
import com.example.recycling_service.dto.Request.CreateAdvertisementRequest;
import com.example.recycling_service.dto.Request.UpdateAdvertisementRequest;
import com.example.recycling_service.exception.ForbiddenException;
import com.example.recycling_service.exception.NotFoundException;
import com.example.recycling_service.model.*;
import com.example.recycling_service.model.Enum.Role;
import com.example.recycling_service.repository.AdvertisementRepository;
import com.example.recycling_service.repository.CategoryRepository;
import com.example.recycling_service.repository.MediaRepository;
import com.example.recycling_service.repository.UserRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
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
        User currentUser = userRepository.findByLogin(userName).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Advertisement ad = advertisementRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Объявление", "Id", id));

        // Проверяем, является ли текущий пользователь владельцем или администратором
        if (!isUserOwner(ad, currentUser) && !currentUser.getRole().equals(Role.ADMIN)) {
            log.warn("У пользователя [{}] нет прав на удаление объявление [{}]", userName, id);
            throw new ForbiddenException(currentUser.getName());
        }

        log.info("Удалено объявление с id {}", id);
        advertisementRepository.delete(ad);
    }

    /**
     * Поиск объявлений, относящихся к ЛЮБОЙ из указанных категорий.
     */
    public PageResponse<AdvertisementResponse> findByCategoryIds(FilterAdvertisementRequest request, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageSize, pageNumber, Sort.by("createdAt").descending());
        Page<Advertisement> page = advertisementRepository.findByCategoryIds(request.getCategoryIds(), pageable);
        PageResponse<AdvertisementResponse> response = new PageResponse<>();
        response.setContent(page.getContent().stream().map(AdvertisementResponse::new).toList());
        response.setTotalElements(page.getTotalElements());
        response.setPageSize(page.getSize());
        response.setPageNumber(page.getNumber());
        return response;
    }

    // Find all advertisements
    public PageResponse<AdvertisementResponse> findAll(int page, int size) {
        Page<Advertisement> result = advertisementRepository
                .findAll(PageRequest.of(page, size, Sort.by("createdAt").descending()));
        PageResponse<AdvertisementResponse> response = new PageResponse<>();
        response.setContent(result.getContent().stream().map(AdvertisementResponse::new).toList());
        response.setPageNumber(result.getNumber());
        response.setPageSize(result.getSize());
        response.setTotalElements(result.getTotalElements());
        return response;
    }

    // Update Advertesiment
    public AdvertisementResponse updateAdvertisement(UUID id, @Valid UpdateAdvertisementRequest request, String login) {
        Advertisement ad = advertisementRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Объявление", "id", id));

        User currentUser = userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь c логин " + login + " не найден"));

        if (!isUserOwner(ad, currentUser) && !currentUser.getRole().equals(Role.ADMIN)){
            log.warn("У пользователя [{}] нет прав на изменение объявление [{}]", login, id);
            throw new ForbiddenException(currentUser.getName());
        }

        // Обновляем только переданные поля (если они не null)
        if (request.getTitle() != null) {
            ad.setTitle(request.getTitle());
            log.info("Обновлен заголовок {}", request.getTitle());
        }
        if (request.getDescription() != null) {
            ad.setDescription(request.getDescription());
            log.info("Обновлено описание {}", request.getDescription());
        }
        if (request.getPrice() != null) {
            ad.setPrice(request.getPrice());
            log.info("Обновлена цена {}", request.getPrice());
        }
        if (request.getCategoryIds() != null) {
            Set<Category> categories = new HashSet<>(categoryRepository.findAllById(request.getCategoryIds()));
            ad.setCategories(categories);
            log.info("Обновлены категории {}", request.getCategoryIds());
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

    public AdvertisementResponse createAdvertisementWithImages(CreateAdvertisementRequest request,
                                                               String username) throws IOException {
        AdvertisementResponse created = createAdvertisement(request, username);

//        if (files != null && !files.isEmpty()) {
//            Advertisement ad = advertisementRepository.findById(created.getId())
//                    .orElseThrow(() -> new RuntimeException("Объявление не найдено"));
//
//            for (MultipartFile file : files) {
//                String fileName = imageStorageService.store(file);
//                Media media = new Media();
//                media.setFilePath("/uploads/" + fileName);
//                media.setMimeType(file.getContentType());
//                ad.getMedia().add(media);
//            }
//
//            advertisementRepository.save(ad);
//        }
        Advertisement ad = advertisementRepository.findById(created.getId())
                    .orElseThrow(() -> new NotFoundException("Объявление", "id", created.getId()));
        advertisementRepository.save(ad);
        return created;
    }


    // create new advertisement
    public AdvertisementResponse createAdvertisement(CreateAdvertisementRequest request, String username) {
        User user = userRepository.findByLogin(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Set<Category> categories = new HashSet<>(categoryRepository.findAllById(request.getCategoryIds()));

        if (categories.size() != request.getCategoryIds().size()) {
            log.warn("Часть категорий не найдена: запрошено [{}], найдено [{}]",
                    request.getCategoryIds().size(), categories.size());
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

    private boolean isUserOwner(Advertisement advertisement, User user) {
        log.info("Проверяем является ли пользователь {} владельцем публикации", user.getLogin());
        return advertisement.getUser().getId().equals(user.getId());
    }
}
