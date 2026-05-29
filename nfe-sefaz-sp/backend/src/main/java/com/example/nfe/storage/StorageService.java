package com.example.nfe.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StorageService {
    private final Path basePath;

    public StorageService(@Value("${app.storage-base-path}") String basePath) {
        this.basePath = Path.of(basePath);
    }

    public String saveText(String relativePath, String content) {
        try {
            Path target = basePath.resolve(relativePath).normalize();
            Files.createDirectories(target.getParent());
            Files.writeString(target, content, StandardCharsets.UTF_8);
            return target.toString();
        } catch (IOException e) {
            throw new IllegalStateException("Falha ao armazenar arquivo fiscal.", e);
        }
    }
}
