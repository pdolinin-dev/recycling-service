package com.example.recycling_service.service;

import com.example.recycling_service.dto.AdvertisementDTO;
import com.example.recycling_service.dto.Request.CreateAdvertisementRequest;
import com.example.recycling_service.dto.Request.UpdateAdvertisementRequest;
import com.example.recycling_service.model.Advertisement;
import com.example.recycling_service.model.Category;
import com.example.recycling_service.model.Media;
import com.example.recycling_service.model.User;
import com.example.recycling_service.repository.AdvertisementRepository;
import com.example.recycling_service.repository.CategoryRepository;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
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


    // Delete advetisement
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
    public List<Advertisement> findByCategoryIds(List<Long> categoryIds) {
        return advertisementRepository.findByCategoryIds(categoryIds);
    }

    //update Advertesiment
    public AdvertisementDTO updateAdvertisement(UUID id, @Valid UpdateAdvertisementRequest request) {
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
        return new AdvertisementDTO(updatedAd);
    }

    private AdvertisementDTO mapToDTO(Advertisement ad) {
        AdvertisementDTO dto = new AdvertisementDTO();
        dto.setId(ad.getId());
        dto.setTitle(ad.getTitle());
        dto.setDescription(ad.getDescription());
        dto.setPrice(ad.getPrice());
        dto.setAddress(ad.getAddress());

        // Добавляем пути к изображениям
        if (ad.getMedia() != null) {
            List<String> mediaUrls = ad.getMedia().stream()
                    .map(Media -> "http://recycling_service:8080" + com.example.recycling_service.model.Media.getFilePath())
                    ;
            dto.setImageUrls(mediaUrls);
        }

        return dto;
    }


    // get all advertisements
    public List<AdvertisementDTO> getAllAdvertisements() {
        return advertisementRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(AdvertisementDTO::new)
                .collect(Collectors.toList());
    }

    public AdvertisementDTO createAdvertisementWithImages(CreateAdvertisementRequest request, List<MultipartFile> files, String username) throws IOException {
        AdvertisementDTO created = createAdvertisement(request, username);

        if (files != null && !files.isEmpty()) {
            Advertisement ad = advertisementRepository.findById(created.getId())
                    .orElseThrow(() -> new RuntimeException("Объявление не найдено"));

            for (MultipartFile file : files) {
                String fileName = imageStorageService.store(file);
                PostImage image = new PostImage();
                image.setFilePath("/uploads/" + fileName);
                image.setMimeType(file.getContentType());
                image.setAdvertisement(ad); // <--- не забудь связать
                ad.getImages().add(image);
            }

            advertisementRepository.save(ad);
        }

        return created;
    }


    // create new advertisement
    public AdvertisementDTO createAdvertisement(CreateAdvertisementRequest request, String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Set<Category> categories = categoryRepository.findAllById(request.getCategoryIds())
                .stream()
                .collect(Collectors.toSet());

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
        return new AdvertisementDTO(savedAd);
    }

    // get advertisement by id
    public AdvertisementDTO getAdvertisementById(UUID id) {
        Advertisement advertisement = advertisementRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Advertisement not found with id: " + id
                ));
        return new AdvertisementDTO(advertisement);
    }

    /**
     * КАТЕГОРИИ
     */
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}
