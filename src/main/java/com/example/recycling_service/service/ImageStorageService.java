package com.example.recycling_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Service
public class ImageStorageService {
    @Value("${upload.path}")
    private String uploadPath;

    public String store(MultipartFile file) throws IOException {
        String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        filename = UUID.randomUUID() + "_" + filename;

        Path uploadDir = Paths.get(uploadPath);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, uploadDir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        }

        return filename;
    }
}
