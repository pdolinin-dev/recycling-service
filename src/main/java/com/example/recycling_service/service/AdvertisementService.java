package com.example.recycling_service.service;

import com.example.recycling_service.dto.AdvertisementDTO;
import com.example.recycling_service.dto.CreateAdvertisementRequest;
import com.example.recycling_service.dto.UpdateAdvertisementRequest;
import com.example.recycling_service.model.Advertisement;
import com.example.recycling_service.model.Category;
import com.example.recycling_service.model.User;
import com.example.recycling_service.repository.AdvertisementRepository;
import com.example.recycling_service.repository.CategoryRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Validated // Добавляем аннотацию для валидации на уровне сервиса
public class AdvertisementService {
    private final AdvertisementRepository advertisementRepository;
    private final CategoryRepository categoryRepository;
    // Delete advetisement
    public void deleteAdvertisement(Long id, User currentUser) {
        Advertisement ad = advertisementRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Объявление с ID " + id + " не найдено"
                ));

        // Проверяем, является ли текущий пользователь владельцем
        if (!ad.getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Вы не можете удалить это объявление"
            );
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
    public AdvertisementDTO updateAdvertisement(Long id, @Valid UpdateAdvertisementRequest request) {
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

        Advertisement updatedAd = advertisementRepository.save(ad);
        return new AdvertisementDTO(updatedAd);
    }
    // get all advertisements
    public List<AdvertisementDTO> getAllAdvertisements() {
        return advertisementRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(AdvertisementDTO::new)
                .collect(Collectors.toList());
    }

    // create new advertisement
    public AdvertisementDTO createAdvertisement(CreateAdvertisementRequest request, User user) {
        // Проверяем обязательные поля
//        if (user == null) {
//            throw new IllegalArgumentException("Authenticated user required");
//        }

        // Получаем категории из БД
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
    public AdvertisementDTO getAdvertisementById(Long id) {
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
